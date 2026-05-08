package com.sloth.boot.example.service;

import com.sloth.boot.starter.ai.core.AiChatClient;
import com.sloth.boot.starter.ai.dto.ChatRequest;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class AiDemoService {

    @Lazy
    @Autowired
    private AiChatClient aiChatClient;

    /**
     * 简单对话。
     */
    public String chat(String prompt) {
        return aiChatClient.chat(prompt);
    }

    /**
     * 流式对话，逐 token 返回。
     */
    public Flux<String> chatStream(String prompt) {
        return aiChatClient.chatStream(prompt);
    }

    /**
     * 带会话记忆的多轮对话。
     */
    public ChatResponse chatWithMemory(String prompt, String conversationId) {
        ChatRequest request = ChatRequest.builder()
                .userPrompt(prompt)
                .conversationId(conversationId)
                .build();
        return aiChatClient.chat(request);
    }

    /**
     * 结构化输出对话，将响应解析为指定类型。
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> chatStructured(String prompt) {
        return aiChatClient.chat(prompt, Map.class);
    }
}
