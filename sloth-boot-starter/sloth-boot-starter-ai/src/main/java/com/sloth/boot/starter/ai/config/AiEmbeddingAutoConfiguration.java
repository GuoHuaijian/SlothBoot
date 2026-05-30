package com.sloth.boot.starter.ai.config;

import com.sloth.boot.starter.ai.core.AiEmbeddingClient;
import com.sloth.boot.starter.ai.core.SpringAiEmbeddingClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * AI 向量嵌入自动配置。
 * <p>
 * 当 classpath 中存在 EmbeddingModel 且未禁用时，自动注册 AiEmbeddingClient。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration(after = AiAutoConfiguration.class)
@ConditionalOnClass(EmbeddingModel.class)
@ConditionalOnProperty(prefix = "sloth.ai.embedding", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AiEmbeddingAutoConfiguration {

    /**
     * 注册向量嵌入客户端。
     *
     * @param embeddingModel Spring AI EmbeddingModel
     * @return AI 向量嵌入客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public AiEmbeddingClient aiEmbeddingClient(EmbeddingModel embeddingModel) {
        return new SpringAiEmbeddingClient(embeddingModel);
    }
}
