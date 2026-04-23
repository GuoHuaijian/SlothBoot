package com.sloth.boot.starter.ai.config;

import com.sloth.boot.starter.ai.core.AiChatClient;
import com.sloth.boot.starter.ai.core.SpringAiChatClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * AI Starter 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(ChatClient.class)
@ConditionalOnProperty(prefix = "sloth.ai", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AiProperties.class)
public class AiAutoConfiguration {

    /**
     * 注册默认的 Spring AI ChatClient。
     *
     * @param chatModel    Spring AI ChatModel
     * @param aiProperties    AI 配置
     * @return Spring AI ChatClient
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothAiChatClient")
    public ChatClient slothAiChatClient(ChatModel chatModel, AiProperties aiProperties) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        if (StringUtils.hasText(aiProperties.getDefaultSystemPrompt())) {
            builder.defaultSystem(aiProperties.getDefaultSystemPrompt());
        }
        builder.defaultOptions(OpenAiChatOptions.builder()
            .model(aiProperties.getModel())
            .temperature(aiProperties.getTemperature())
            .topP(aiProperties.getTopP())
            .maxTokens(aiProperties.getMaxTokens())
            .build());
        return builder.build();
    }

    /**
     * 注册统一 AI Chat Client。
     *
     * @param chatClient Spring AI ChatClient
     * @return AI Chat Client
     */
    @Bean
    @ConditionalOnMissingBean
    public AiChatClient aiChatClient(ChatClient chatClient) {
        return new SpringAiChatClient(chatClient);
    }
}
