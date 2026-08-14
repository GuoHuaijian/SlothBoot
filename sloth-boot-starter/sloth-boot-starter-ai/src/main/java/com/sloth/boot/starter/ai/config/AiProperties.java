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

    /**
     * 对话记忆配置。
     */
    private Memory memory = new Memory();

    /**
     * 向量嵌入配置。
     */
    private Embedding embedding = new Embedding();

    /**
     * 图像生成配置。
     */
    private Image image = new Image();

    /**
     * 可观测性配置。
     */
    private Observability observability = new Observability();

    /**
     * 对话记忆配置。
     */
    @Data
    public static class Memory {

        /**
         * 是否启用对话记忆。
         */
        private boolean enabled = false;

        /**
         * 滑动窗口最大消息数。
         */
        private int maxMessages = 20;
    }

    /**
     * 向量嵌入配置。
     */
    @Data
    public static class Embedding {

        /**
         * 是否启用向量嵌入客户端。
         */
        private boolean enabled = true;
    }

    /**
     * 图像生成配置。
     */
    @Data
    public static class Image {

        /**
         * 是否启用图像生成客户端。
         */
        private boolean enabled = true;
    }

    /**
     * 可观测性配置。
     */
    @Data
    public static class Observability {

        /**
         * 是否启用 AI 可观测性装饰器。
         */
        private boolean enabled = true;

        /**
         * 慢调用阈值（毫秒）。
         */
        private long slowThresholdMs = 3000;
    }
}
