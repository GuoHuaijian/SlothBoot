package com.sloth.boot.starter.ai.core;

import com.sloth.boot.starter.ai.dto.ChatRequest;
import com.sloth.boot.starter.ai.dto.ChatResponse;
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

    /**
     * 发送结构化对话请求，返回完整响应（含 Token 用量）。
     *
     * @param request 对话请求
     * @return 对话响应
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 发送结构化对话请求，流式返回响应文本。
     *
     * @param request 对话请求
     * @return 响应文本流
     */
    Flux<String> chatStream(ChatRequest request);

    /**
     * 发送对话并将响应解析为指定类型的对象。
     *
     * @param userPrompt 用户提示词
     * @param type       目标类型
     * @param <T>        泛型
     * @return 解析后的对象实例
     */
    <T> T chat(String userPrompt, Class<T> type);

    /**
     * 发送对话并将响应解析为指定类型的对象（带系统提示词）。
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @param type         目标类型
     * @param <T>          泛型
     * @return 解析后的对象实例
     */
    <T> T chat(String systemPrompt, String userPrompt, Class<T> type);
}
