package com.guidance.repository;

import com.alibaba.dashscope.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    public PersistentChatMemoryStore(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init(){
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "    id SERIAL PRIMARY KEY," +
                        "    chat_id VARCHAR(255) NOT NULL," +
                        "    message_type VARCHAR(50) NOT NULL," +
                        "    text_content TEXT NOT NULL," +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");\n" +
                        "CREATE INDEX IF NOT EXISTS idx_chat_id ON chat_messages(chat_id);",
                "chat_messages"
        );
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create chat_messages table", e);
        }
    }
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String sql = "SELECT message_type, text_content FROM chat_messages WHERE chat_id = ? ORDER BY id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String type = rs.getString("message_type");
            String text = rs.getString("text_content");
            switch (type) {
                case "USER":
                    List<Content> contents = JsonUtils.fromJson(text,new TypeToken<List<TextContent>>(){}.getType());
                    return new UserMessage(contents);
                case "AI": return new AiMessage(text);
                case "SYSTEM": return new SystemMessage(text);
                default: throw new IllegalArgumentException("Unknown message type: " + type);
            }
        }, memoryId.toString());
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // LangChain4j 的 updateMessages 通常是全量更新或追加
        // 为了简单高效，我们这里实现为“追加新消息”的逻辑
        // 注意：实际生产中需要处理“删除旧消息”（Window机制）的情况
        // 这里为了演示核心逻辑，我们假设每次只存最新的
        
        // 这里的 messages 参数实际上包含了当前窗口内的所有消息
        // 生产环境建议：先删除该 chat_id 的旧记录，再批量插入（简单粗暴但有效保持一致性）
        // 或者更复杂的逻辑：对比差异只插入新的。
        
        deleteMessages(memoryId); // 简单策略：清除旧窗口，写入新窗口

        String sql = "INSERT INTO chat_messages (chat_id, message_type, text_content) VALUES (?, ?, ?)";
        
        for (ChatMessage message : messages) {
            String type;
            String content;
            if (message instanceof UserMessage) {
                type = "USER";
                content= "";
                List<Content> contents = ((UserMessage)message).contents();
                if(!CollectionUtils.isEmpty(contents)){
                    //将content数组序列化成json字符串
                    content = JsonUtils.toJson(contents);
                }
            }
            else if (message instanceof AiMessage) {
                type = "AI";
                content=((AiMessage)message).text();
            }
            else if (message instanceof SystemMessage){
                type = "SYSTEM";
                content=((SystemMessage)message).text();
            }
            else continue;

            jdbcTemplate.update(sql, memoryId.toString(), type, content);
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        jdbcTemplate.update("DELETE FROM chat_messages WHERE chat_id = ?", memoryId.toString());
    }
}