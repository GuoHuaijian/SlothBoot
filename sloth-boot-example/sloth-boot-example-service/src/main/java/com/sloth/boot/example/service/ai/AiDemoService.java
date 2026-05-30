package com.sloth.boot.example.service.ai;

import com.sloth.boot.starter.ai.core.AiChatClient;
import com.sloth.boot.starter.ai.dto.ChatRequest;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI 演示服务 - 展示 AI 对话、流式输出、多轮记忆、结构化输出等能力
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(AiChatClient.class)
public class AiDemoService {

    @Lazy
    private final AiChatClient aiChatClient;

    /**
     * 简单对话。
     *
     * @param prompt 用户输入
     * @return AI 回复文本
     */
    public String chat(String prompt) {
        return aiChatClient.chat(prompt);
    }

    /**
     * 流式对话，逐 token 返回。
     *
     * @param prompt 用户输入
     * @return 流式文本响应
     */
    public Flux<String> chatStream(String prompt) {
        return aiChatClient.chatStream(prompt);
    }

    /**
     * 带会话记忆的多轮对话。
     *
     * @param prompt         用户输入
     * @param conversationId 会话ID
     * @return 对话响应
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
     *
     * @param prompt 用户输入
     * @return 结构化输出结果
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> chatStructured(String prompt) {
        return aiChatClient.chat(prompt, Map.class);
    }
}
