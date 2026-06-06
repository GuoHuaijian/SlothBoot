package com.sloth.boot.starter.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "统一 AI 对话响应")
public class ChatResponse {

    /**
     * 响应文本。
     */
    @Schema(description = "响应文本", example = "Spring Boot 是一个...")
    private String content;

    /**
     * 模型名称。
     */
    @Schema(description = "模型名称", example = "gpt-4o")
    private String model;

    /**
     * 输入 Token 数。
     */
    @Schema(description = "输入 Token 数", example = "100")
    private Long promptTokens;

    /**
     * 输出 Token 数。
     */
    @Schema(description = "输出 Token 数", example = "500")
    private Long completionTokens;

    /**
     * 总 Token 数。
     */
    @Schema(description = "总 Token 数", example = "600")
    private Long totalTokens;

    /**
     * 完成原因。
     */
    @Schema(description = "完成原因", example = "STOP")
    private String finishReason;
}
