package com.sloth.boot.starter.ai.dto;

import java.util.List;
import java.util.Map;

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
public class ChatRequest {

    /**
     * 用户提示词。
     */
    private String userPrompt;

    /**
     * 系统提示词（可选，不传则使用默认）。
     */
    private String systemPrompt;

    /**
     * 会话 ID，用于多轮对话记忆（可选）。
     */
    private String conversationId;

    /**
     * 模型名称覆盖（可选，不传则使用默认）。
     */
    private String model;

    /**
     * 温度参数覆盖（可选）。
     */
    private Double temperature;

    /**
     * topP 参数覆盖（可选）。
     */
    private Double topP;

    /**
     * 最大输出 Token 数覆盖（可选）。
     */
    private Integer maxTokens;

    /**
     * 附加用户消息变量，用于模板渲染。
     */
    private Map<String, Object> variables;

    /**
     * 工具对象列表，用于本次请求的工具调用（可选）。
     */
    private List<Object> tools;
}
