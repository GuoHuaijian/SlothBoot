package com.sloth.boot.starter.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sloth.boot.starter.ai.core.AiChatClient;
import com.sloth.boot.starter.ai.core.SpringAiChatClient;
import com.sloth.boot.starter.ai.function.AiFunctionRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
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
     * 注册默认的对话记忆（滑动窗口策略）。
     *
     * @param aiProperties AI 配置
     * @return ChatMemory 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.ai.memory", name = "enabled", havingValue = "true")
    public ChatMemory slothChatMemory(AiProperties aiProperties) {
        return MessageWindowChatMemory.builder()
            .maxMessages(aiProperties.getMemory().getMaxMessages())
            .build();
    }

    /**
     * 注册默认的 Spring AI ChatClient。
     *
     * @param chatModel    Spring AI ChatModel
     * @param aiProperties AI 配置
     * @param chatMemory   对话记忆（可选）
     * @return Spring AI ChatClient
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothAiChatClient")
    public ChatClient slothAiChatClient(ChatModel chatModel, AiProperties aiProperties,
                                        @Autowired(required = false) ChatMemory chatMemory) {
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
     * @param chatClient   Spring AI ChatClient
     * @param chatMemory   对话记忆（可选）
     * @param objectMapper JSON 序列化器
     * @return AI Chat Client
     */
    @Bean
    @ConditionalOnMissingBean
    public AiChatClient aiChatClient(ChatClient chatClient, @Autowired(required = false) ChatMemory chatMemory,
                                     ObjectMapper objectMapper) {
        return new SpringAiChatClient(chatClient, chatMemory, objectMapper);
    }

    /**
     * 注册 AI 函数调用注册中心。
     *
     * @return 函数注册中心
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.ai.function", name = "enabled", havingValue = "true")
    public AiFunctionRegistry aiFunctionRegistry() {
        return new AiFunctionRegistry();
    }
}
