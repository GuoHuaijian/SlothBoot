package com.sloth.boot.starter.ai.core;

import reactor.core.publisher.Flux;

/**
 * 统一 AI 对话客户端。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface AiChatClient {

    /**
     * 使用默认系统提示词发送单轮对话。
     *
     * @param userPrompt 用户提示词
     * @return 响应文本
     */
    String chat(String userPrompt);

    /**
     * 使用指定系统提示词发送单轮对话。
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return 响应文本
     */
    String chat(String systemPrompt, String userPrompt);

    /**
     * 流式对话（SSE），逐 token 返回响应。
     *
     * @param userPrompt 用户提示词
     * @return 响应文本流
     */
    Flux<String> chatStream(String userPrompt);

    /**
     * 流式对话（SSE），带系统提示词。
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return 响应文本流
     */
    Flux<String> chatStream(String systemPrompt, String userPrompt);
}
