package com.guidance.config;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static dev.langchain4j.data.message.ContentType.TEXT;

@Slf4j
public class MyChatModelListener implements ChatModelListener {

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        List<ChatMessage> messages = requestContext.request().messages();
        for(ChatMessage msg : messages){
            if(msg.type() == ChatMessageType.SYSTEM ){
                log.info("system-message: {}", ((SystemMessage)msg).text());
            }
            if(msg.type() == ChatMessageType.USER ){
                UserMessage userMessage =  ((UserMessage)msg);
                if(!CollectionUtils.isEmpty(userMessage.contents())){

                    for(Content content : userMessage.contents()){
                        if(TEXT == content.type()){
                            log.info("user-text-message-{}:{}",Thread.currentThread().getName(),((TextContent)content).text());
                            continue;
                        }
                        log.info("user-other-message: {}",content);
                    }
                }
            }
        }
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        log.info("onResponse===>: {}", responseContext.response());
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        log.info("onError===>: {}", errorContext.error().getMessage());
    }
}
