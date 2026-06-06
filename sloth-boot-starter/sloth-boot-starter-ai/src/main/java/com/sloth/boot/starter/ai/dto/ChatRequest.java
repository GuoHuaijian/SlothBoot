package com.sloth.boot.starter.ai.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 统一 AI 对话请求。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
@Schema(description = "统一 AI 对话请求")
public class ChatRequest {

    /**
     * 用户提示词。
     */
    @Schema(description = "用户提示词", example = "介绍一下 Spring Boot")
    private String userPrompt;

    /**
     * 系统提示词（可选，不传则使用默认）。
     */
    @Schema(description = "系统提示词（可选，不传则使用默认）")
    private String systemPrompt;

    /**
     * 会话 ID，用于多轮对话记忆（可选）。
     */
    @Schema(description = "会话ID，用于多轮对话记忆", example = "conv-001")
    private String conversationId;

    /**
     * 模型名称覆盖（可选，不传则使用默认）。
     */
    @Schema(description = "模型名称覆盖（可选）", example = "gpt-4o")
    private String model;

    /**
     * 温度参数覆盖（可选）。
     */
    @Schema(description = "温度参数，控制输出随机性（可选）", example = "0.7")
    private Double temperature;

    /**
     * topP 参数覆盖（可选）。
     */
    @Schema(description = "Top-P 采样参数（可选）", example = "0.9")
    private Double topP;

    /**
     * 最大输出 Token 数覆盖（可选）。
     */
    @Schema(description = "最大输出 Token 数（可选）", example = "2048")
    private Integer maxTokens;

    /**
     * 附加用户消息变量，用于模板渲染。
     */
    @Schema(description = "附加用户消息变量，用于模板渲染")
    private Map<String, Object> variables;

    /**
     * 工具对象列表，用于本次请求的工具调用（可选）。
     */
    @Schema(description = "工具对象列表，用于本次请求的工具调用（可选）")
    private List<Object> tools;
}
