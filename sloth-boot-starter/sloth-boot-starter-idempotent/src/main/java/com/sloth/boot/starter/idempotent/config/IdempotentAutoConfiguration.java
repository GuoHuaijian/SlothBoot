package com.sloth.boot.starter.idempotent.config;

import com.sloth.boot.starter.idempotent.aspect.IdempotentAspect;
import com.sloth.boot.starter.idempotent.core.TokenIdempotentService;
import com.sloth.boot.starter.idempotent.support.SpelKeyResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 幂等自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnProperty(prefix = "sloth.idempotent", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(IdempotentProperties.class)
public class IdempotentAutoConfiguration {

    /**
     * 注册 SpEL Key 解析器。
     *
     * @return SpEL Key 解析器
     */
    @Bean
    @ConditionalOnMissingBean
    public SpelKeyResolver spelKeyResolver() {
        return new SpelKeyResolver();
    }

    /**
     * 注册 Token 幂等服务。
     *
     * @param stringRedisTemplate Redis 模板
     * @param idempotentProperties 幂等配置
     * @return Token 幂等服务
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenIdempotentService tokenIdempotentService(StringRedisTemplate stringRedisTemplate,
                                                         IdempotentProperties idempotentProperties) {
        return new TokenIdempotentService(stringRedisTemplate, idempotentProperties);
    }

    /**
     * 注册幂等切面。
     *
     * @param stringRedisTemplate Redis 模板
     * @param idempotentProperties 幂等配置
     * @param spelKeyResolver SpEL 解析器
     * @return 幂等切面
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentAspect enhancedIdempotentAspect(StringRedisTemplate stringRedisTemplate,
                                                     IdempotentProperties idempotentProperties,
                                                     SpelKeyResolver spelKeyResolver) {
        return new IdempotentAspect(stringRedisTemplate, idempotentProperties, spelKeyResolver);
    }
}
