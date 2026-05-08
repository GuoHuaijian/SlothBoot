package com.sloth.boot.starter.ai.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 统一 AI 对话响应。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
public class ChatResponse {

    /**
     * 响应文本。
     */
    private String content;

    /**
     * 模型名称。
     */
    private String model;

    /**
     * 输入 Token 数。
     */
    private Long promptTokens;

    /**
     * 输出 Token 数。
     */
    private Long completionTokens;

    /**
     * 总 Token 数。
     */
    private Long totalTokens;

    /**
     * 完成原因。
     */
    private String finishReason;
}
