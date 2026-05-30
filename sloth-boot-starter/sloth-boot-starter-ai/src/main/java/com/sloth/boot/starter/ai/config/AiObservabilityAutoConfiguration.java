package com.sloth.boot.starter.ai.config;

import com.sloth.boot.starter.ai.core.AiChatClient;
import com.sloth.boot.starter.ai.support.AiChatClientDecorator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * AI 可观测性自动配置。
 * <p>
 * 当未禁用时，自动注册 AiChatClientDecorator 装饰器， 为 AiChatClient 添加日志、慢调用告警和 Token 统计能力。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration(after = AiAutoConfiguration.class)
@ConditionalOnClass(AiChatClient.class)
@ConditionalOnProperty(prefix = "sloth.ai.observability", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AiObservabilityAutoConfiguration {

    /**
     * 注册 AI 对话客户端可观测性装饰器。
     *
     * @param aiChatClient AI 对话客户端
     * @param aiProperties AI 配置
     * @return 装饰后的 AI 对话客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public AiChatClientDecorator aiChatClientDecorator(AiChatClient aiChatClient, AiProperties aiProperties) {
        return new AiChatClientDecorator(aiChatClient, aiProperties.getObservability());
    }
}
