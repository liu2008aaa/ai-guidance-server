package com.guidance.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface GovAssistant {
    @SystemMessage(fromResource = "prompt.md")
    TokenStream chat(@MemoryId String userId, @UserMessage String userQuery);
}