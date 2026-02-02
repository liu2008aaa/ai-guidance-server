package com.guidance.controller;

import com.guidance.service.GovAssistant;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin // 允许跨域调试
@Slf4j
public class ChatController {

    private final GovAssistant assistant;
    // 用于异步发送消息的线程池
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ChatController(GovAssistant assistant) {
        this.assistant = assistant;
    }

    /**
     * 流式聊天接口
     * @param chatId 聊天会话ID (用于区分不同用户/对话)
     * @param message 用户消息
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam String chatId, @RequestParam String message) {
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时

        executor.execute(() -> {
            try {
                TokenStream tokenStream = assistant.chat(chatId, message);

                tokenStream
                    .onNext(token -> {
                        try {
                            // 发送具体内容
                            emitter.send(token);
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    })
                    .onComplete(token -> {
                        // 完成时发送特定信号或直接关闭
                        emitter.complete();
                    })
                    .onError(emitter::completeWithError)
                    .start();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}