package com.sloth.boot.starter.idempotent.config;

import com.sloth.boot.starter.idempotent.aspect.IdempotentAspect;
import com.sloth.boot.starter.idempotent.core.TokenIdempotentService;
import com.sloth.boot.starter.idempotent.metrics.IdempotentMetrics;
import com.sloth.boot.starter.idempotent.spi.DefaultIdempotentKeyStrategy;
import com.sloth.boot.starter.idempotent.spi.IdempotentKeyStrategy;
import com.sloth.boot.starter.idempotent.spi.IdempotentStore;
import com.sloth.boot.starter.idempotent.spi.RedisIdempotentStore;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.ObjectProvider;
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

    @Bean
    @ConditionalOnMissingBean
    public TokenIdempotentService tokenIdempotentService(StringRedisTemplate stringRedisTemplate,
                                                         IdempotentProperties idempotentProperties) {
        return new TokenIdempotentService(stringRedisTemplate, idempotentProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentStore idempotentStore(StringRedisTemplate stringRedisTemplate) {
        return new RedisIdempotentStore(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentKeyStrategy idempotentKeyStrategy(IdempotentProperties properties) {
        return new DefaultIdempotentKeyStrategy(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(MeterRegistry.class)
    public IdempotentMetrics idempotentMetrics(MeterRegistry meterRegistry) {
        return new IdempotentMetrics(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentAspect idempotentAspect(StringRedisTemplate stringRedisTemplate,
                                              IdempotentProperties idempotentProperties,
                                              ObjectProvider<TokenIdempotentService> tokenServiceProvider) {
        return new IdempotentAspect(stringRedisTemplate, idempotentProperties, tokenServiceProvider.getIfAvailable());
    }
}
