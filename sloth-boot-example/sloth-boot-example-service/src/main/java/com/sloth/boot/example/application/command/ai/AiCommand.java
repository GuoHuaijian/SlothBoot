package com.sloth.boot.example.application.command.ai;

import com.sloth.boot.starter.ai.core.AiChatClient;
import com.sloth.boot.starter.ai.dto.ChatRequest;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI 演示服务 - 展示 AI 对话、流式输出、多轮记忆、结构化输出等能力
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(AiChatClient.class)
public class AiCommand {

    @Lazy
    private final AiChatClient aiChatClient;

    /**
     * 简单对话。
     * <p>
     * 发送提示词获取AI回复文本，适用于单轮对话场景。
     *
     * @param prompt 用户提示词
     * @return AI回复文本
     */
    public String chat(String prompt) {
        return aiChatClient.chat(prompt);
    }

    /**
     * 流式对话，逐token返回。
     * <p>
     * 适用于打字机效果的流式输出场景，适合前端实时显示。
     *
     * @param prompt 用户提示词
     * @return 流式响应，每个元素为一个token
     */
    public Flux<String> chatStream(String prompt) {
        return aiChatClient.chatStream(prompt);
    }

    /**
     * 带会话记忆的多轮对话。
     * <p>
     * 通过会话ID关联历史上下文，AI会记住之前的对话内容。
     *
     * @param prompt         用户提示词
     * @param conversationId 会话ID，用于关联多轮对话
     * @return 聊天响应，包含回复内容和会话ID
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
     * <p>
     * AI将响应解析为结构化JSON格式，适用于需要结构化数据的场景。
     *
     * @param prompt 用户提示词
     * @return 结构化数据（Map格式）
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> chatStructured(String prompt) {
        return aiChatClient.chat(prompt, Map.class);
    }
}
