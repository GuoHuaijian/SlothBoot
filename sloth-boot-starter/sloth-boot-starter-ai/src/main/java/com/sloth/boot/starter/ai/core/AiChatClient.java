package com.sloth.boot.starter.ai.core;

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
}
