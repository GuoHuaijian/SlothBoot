package com.sloth.boot.starter.ai.support;

import com.sloth.boot.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AI 模块错误码。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum AiErrorCode implements ErrorCode {

    /**
     * 用户提示词为空。
     */
    EMPTY_PROMPT(1900, "AI 提示词不能为空"),

    /**
     * AI 服务提供商调用失败。
     */
    PROVIDER_ERROR(1901, "AI 服务提供商调用失败"),

    /**
     * AI 图像生成失败。
     */
    IMAGE_GENERATION_ERROR(1902, "AI 图像生成失败"),

    /**
     * AI 向量嵌入生成失败。
     */
    EMBEDDING_ERROR(1903, "AI 向量嵌入生成失败"),

    /**
     * AI 对话记忆操作失败。
     */
    MEMORY_ERROR(1904, "AI 对话记忆操作失败"),

    /**
     * AI 工具调用失败。
     */
    TOOL_CALL_ERROR(1905, "AI 工具调用失败"),

    /**
     * AI 服务调用超时。
     */
    TIMEOUT(1906, "AI 服务调用超时"),

    /**
     * AI 服务请求频率超限。
     */
    RATE_LIMITED(1907, "AI 服务请求频率超限"),

    /**
     * AI 结构化输出解析失败。
     */
    STRUCTURED_OUTPUT_ERROR(1908, "AI 结构化输出解析失败");

    private final int code;

    private final String msg;
}
