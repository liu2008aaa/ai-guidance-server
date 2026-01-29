package com.guidance.service;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.guidance.utils.StringUtils.truncateWithSuffix;
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
        String text = expandQuery(query);
        log.info("[retrieve] chatId:{},embedding.content:{}",query.metadata().chatMemoryId(),text);
        Embedding embeddedQuery = embeddingModel.embed(text).content();

        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(embeddedQuery)
                .maxResults(20)//返回最多20条向量匹配后的数据
                .minScore(0.6)
                .filter(null)//如何使用？
                .build();

        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

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
                    text = text.substring(0,text.indexOf("\n"));
                    sb.append(text);
                    sb.append("\n");
                }
            }
            //仅获取上一条会话内容
            break;
        }
        return truncateWithSuffix(sb.toString(),2048);
    }
}