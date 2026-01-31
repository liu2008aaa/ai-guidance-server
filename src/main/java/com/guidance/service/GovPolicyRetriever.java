package com.guidance.service;

import com.guidance.constants.RegionEnum;
import com.guidance.utils.RegionExtractorByHanLP;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.guidance.utils.StringUtils.truncateWithSuffix;
import static dev.langchain4j.internal.Utils.isNotNullOrBlank;
import static dev.langchain4j.internal.Utils.isNullOrBlank;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static java.util.stream.Collectors.toList;

/**
 * 自定义Retriever
 *
 * @author liuyu
 */
@Slf4j
public class GovPolicyRetriever implements ContentRetriever {
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    public GovPolicyRetriever(EmbeddingStore<TextSegment> embeddingStore,EmbeddingModel embeddingModel){
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }
    @Override
    public List<Content> retrieve(Query query) {
        String userInputText = query.text();
        log.info("[retrieve] chatId:{},userInputText:{}",query.metadata().chatMemoryId(),userInputText);
        //扩展用户输入，追加上次输入
        String expandText = expandQuery(query);
        log.info("[retrieve] chatId:{},embedding.content:{}",query.metadata().chatMemoryId(),expandText);
        //调用大模型计算向量
        Embedding embeddedQuery = embeddingModel.embed(expandText).content();
        //构建查询器
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(embeddedQuery)
                .maxResults(20)//返回最多20条向量匹配后的数据
                .minScore(0.65)
                .filter(makeFilter(userInputText))//区域过滤器
                .build();
        //向量数据库搜索
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);
        //返回搜索结果
        return searchResult.matches().stream()
                .map(EmbeddingMatch::embedded)
                .map(Content::from)
                .collect(toList());
    }

    /**
     * 基于用户历史对话信息增强 模型的向量准确性
     *
     * @param query
     * @return
     */
    private String expandQuery(Query query) {
        //获取用户历史对话信息
        List<ChatMessage> chatMessageList = query.metadata().chatMemory();
        if(CollectionUtils.isEmpty(chatMessageList)){
            return truncateWithSuffix(query.text(),2048);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(query.text());
        sb.append("\n");
        //会话内容倒序
        for(int i = chatMessageList.size() - 1; i >=0 ; i--){
            ChatMessage chatMessage = chatMessageList.get(i);
            //只取用户输入信息
            if(chatMessage.type() != ChatMessageType.USER){
                continue;
            }
            List<dev.langchain4j.data.message.Content> contents = ((UserMessage)chatMessage).contents();
            if(CollectionUtils.isEmpty(contents)){
                continue;
            }
            for(dev.langchain4j.data.message.Content content : contents){
                if(ContentType.TEXT == content.type()){
                    String text = ((TextContent)content).text();
                    //截取用户每次输入的message
                    if(text.contains("\n")) {
                        text = text.substring(0, text.indexOf("\n"));
                        sb.append(text);
                        sb.append("\n");
                    }
                }
            }
            //仅获取上一条会话内容
            break;
        }
        return truncateWithSuffix(sb.toString(),2048);
    }

    /**
     * 通过用户的输入信息构建 filter
     *
     * @param userInputText
     * @return
     */
    public static Filter makeFilter(String userInputText) {
        //解析区域信息
        RegionExtractorByHanLP.RegionResult regionResult = RegionExtractorByHanLP.parseAddress(userInputText);
        if(regionResult == null){
            return null;
        }
        // 构建 filter
        List<Filter> regionFilters = new ArrayList<>();
        if(isNotNullOrBlank(regionResult.province)){
            regionFilters.add(metadataKey(RegionEnum.province.name()).isEqualTo(regionResult.province));
        }else if(isNotNullOrBlank(regionResult.city)){
            regionFilters.add(metadataKey(RegionEnum.city.name()).isEqualTo(regionResult.city));
        }else if(isNotNullOrBlank(regionResult.district)){
            regionFilters.add(metadataKey(RegionEnum.district.name()).isEqualTo(regionResult.district));
        }else if(isNotNullOrBlank(regionResult.town)){
            regionFilters.add(metadataKey(RegionEnum.town.name()).isEqualTo(regionResult.town));
        }else if(isNotNullOrBlank(regionResult.village)){
            regionFilters.add(metadataKey(RegionEnum.village.name()).isEqualTo(regionResult.village));
        }
        return buildAndFilter(regionFilters);
    }
    /**
     * 动态构建N级AND Filter
     */
    public static Filter buildAndFilter(List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        if (filters.size() == 1) {
            return filters.get(0);
        }
        // 从第一个Filter开始链式and
        Filter result = filters.get(0);
        for (int i = 1; i < filters.size(); i++) {
            result = result.and(filters.get(i));
        }
        return result;
    }
}