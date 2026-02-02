package com.guidance.config;

import com.guidance.repository.CompatiblePgVectorEmbeddingStore;
import com.guidance.repository.PureJavaPgVectorStore;
import com.guidance.service.GovAssistant;
import com.guidance.service.GovPolicyRetriever;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

/**
 * 模型配置类
 *
 * @author liuyu001
 */
@Configuration
public class AIModelConfig {
    /**
     * 模型apiKey
     */
    @Value("${ai-model.api-key}")
    private String apiKey;
    /**
     * 模型name
     */
    @Value("${ai-model.model-name}")
    private String modelName;
    /**
     * embedding model
     */
    @Value("${ai-model.embedding-model}")
    private String embeddingModel;

    /**
     * 向量库存储相关配置
     */
    @Value("${rag.pgvector.table}") private String pgTable;
    @Value("${rag.pgvector.dimension}") private Integer pgDimension;

    /**
     * 1. 配置 Qwen 流式模型
     * 显式构建模型，而不是依赖 starter 自动注入
     */
    @Bean
    StreamingChatLanguageModel qwenStreamingChatModel() {
        return QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .listeners(List.of(new MyChatModelListener()))//记录输入、输出日志
                .temperature(0.5f)
                .build();
    }

    /**
     * 2. 配置记忆提供者
     * 决定了如何根据 ChatId 创建记忆，以及记忆存放在哪里(store)
     */
    @Bean
    ChatMemoryProvider chatMemoryProvider(ChatMemoryStore chatMemoryStore) {
        return chatId -> MessageWindowChatMemory.builder()
                .id(chatId)
                .maxMessages(20)
                .chatMemoryStore(chatMemoryStore) // 绑定您的持久化存储实现
                .build();
    }

    /**
     * 3. 嵌入模型 Bean
     */
    @Bean
    EmbeddingModel embeddingModel() {
        return QwenEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(embeddingModel)
                .build();
    }

    /**
     * 4. 向量存储 Bean
     */
    @Bean
    EmbeddingStore<TextSegment> embeddingStore(DataSource dataSource) {
//        return new PureJavaPgVectorStore(dataSource,
//                pgTable,
//                pgDimension);
        return new CompatiblePgVectorEmbeddingStore(dataSource,pgTable,pgDimension);
    }

    /**
     * 5. 检索器 Bean
     */
    @Bean
    ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                      EmbeddingModel embeddingModel) {
        return new GovPolicyRetriever(embeddingStore,embeddingModel);
//        return EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(embeddingStore)
//                .embeddingModel(embeddingModel)
//                .maxResults(20)//每次在向量数据库中返回的条数，太多容易浪费token，太少容易丢失精度
//                .minScore(0.6)
//                .build();
    }

//    @Bean
//    RetrievalAugmentor retrievalAugmentor(){
//        return DefaultRetrievalAugmentor.builder()
//                .build();
//    }
    /**
     * 6. 构建 AI 服务实例并注入 Spring 容器
     */
    @Bean
    GovAssistant govAssistant(StreamingChatLanguageModel chatModel,
                              ChatMemoryProvider chatMemoryProvider,
                              ContentRetriever contentRetriever) {
        return AiServices.builder(GovAssistant.class)
                .streamingChatLanguageModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .contentRetriever(contentRetriever)
//                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }
}