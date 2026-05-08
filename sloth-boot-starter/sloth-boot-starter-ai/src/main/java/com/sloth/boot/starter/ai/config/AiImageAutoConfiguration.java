package com.sloth.boot.starter.ai.config;

import com.sloth.boot.starter.ai.core.AiImageClient;
import com.sloth.boot.starter.ai.core.SpringAiImageClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * AI 图像生成自动配置。
 * <p>
 * 当 classpath 中存在 ImageModel 且未禁用时，自动注册 AiImageClient。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration(after = AiAutoConfiguration.class)
@ConditionalOnClass(ImageModel.class)
@ConditionalOnProperty(prefix = "sloth.ai.image", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AiProperties.class)
public class AiImageAutoConfiguration {

    /**
     * 注册图像生成客户端。
     *
     * @param imageModel Spring AI ImageModel
     * @return AI 图像生成客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public AiImageClient aiImageClient(ImageModel imageModel) {
        return new SpringAiImageClient(imageModel);
    }
}
