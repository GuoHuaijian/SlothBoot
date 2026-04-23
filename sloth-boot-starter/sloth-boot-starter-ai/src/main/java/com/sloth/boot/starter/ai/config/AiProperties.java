package com.sloth.boot.starter.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI Starter 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.ai")
public class AiProperties {

    /**
     * 默认模型名称。
     */
    public static final String DEFAULT_MODEL = "gpt-4o-mini";

    /**
     * 是否启用 AI Starter。
     */
    private boolean enabled = true;

    /**
     * 默认模型名称。
     */
    private String model = DEFAULT_MODEL;

    /**
     * 默认温度参数。
     */
    private Double temperature = 0.7D;

    /**
     * 默认 topP 参数。
     */
    private Double topP = 1.0D;

    /**
     * 默认最大输出 Token 数。
     */
    private Integer maxTokens = 2048;

    /**
     * 默认系统提示词。
     */
    private String defaultSystemPrompt;
}
