package com.guidance.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface GovAssistant {

    @SystemMessage({
        "你是一个专业的政府办事指南助手。",
        "你的任务是根据提供的上下文信息（Context），准确回答用户关于政务办理的问题。",
        "请务必按照以下结构整理回答：",
        "1. **受理条件**",
        "2. **办理流程**",
        "3. **申请材料**",
        "4. **收费标准与依据**",
        "如果上下文中没有包含某项信息，请明确说明'暂无相关信息'，严禁编造。",
        "回答风格要亲切、严谨、官方。"
    })
    TokenStream chat(@MemoryId String userId, @UserMessage String userQuery);
}