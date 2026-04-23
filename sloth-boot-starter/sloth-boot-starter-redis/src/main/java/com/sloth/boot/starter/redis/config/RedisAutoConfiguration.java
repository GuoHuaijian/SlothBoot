package com.sloth.boot.starter.redis.config;

import com.sloth.boot.starter.redis.core.RedisCacheUtil;
import com.sloth.boot.starter.redis.delay.RedisDelayQueue;
import com.sloth.boot.starter.redis.idempotent.IdempotentAspect;
import com.sloth.boot.starter.redis.limiter.RateLimiterAspect;
import com.sloth.boot.starter.redis.lock.DistributedLock;
import com.sloth.boot.starter.redis.lock.DistributedLockAspect;
import com.sloth.boot.starter.redis.lock.RedissonDistributedLock;
import com.sloth.boot.starter.redis.monitor.RedisHealthIndicator;
import org.redisson.api.RedissonClient;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Redis 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration(after = org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
@ConditionalOnClass({RedisTemplate.class, StringRedisTemplate.class})
@ConditionalOnProperty(prefix = "sloth.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RedisProperties.class)
@Import(RedisSerializerConfig.class)
public class RedisAutoConfiguration {

    /**
     * 注册 RedisTemplate。
     *
     * @param redisConnectionFactory            Redis 连接工厂
     * @param genericJackson2JsonRedisSerializer JSON 序列化器
     * @return RedisTemplate
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "slothRedisTemplate")
    public RedisTemplate<String, Object> slothRedisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 注册 StringRedisTemplate。
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return StringRedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    /**
     * 注册 Redis 缓存工具类。
     *
     * @param redisTemplate   RedisTemplate
     * @param redisProperties Redis 配置
     * @return Redis 缓存工具类
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisCacheUtil redisCacheUtil(@Qualifier("slothRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                                         RedisProperties redisProperties) {
        return new RedisCacheUtil(redisTemplate, redisProperties);
    }

    /**
     * 注册分布式锁实现。
     *
     * @param redissonClient Redisson 客户端
     * @return 分布式锁
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedissonClient.class)
    public DistributedLock distributedLock(RedissonClient redissonClient) {
        return new RedissonDistributedLock(redissonClient);
    }

    /**
     * 注册分布式锁切面。
     *
     * @param distributedLock 分布式锁
     * @param redisProperties Redis 配置
     * @return 分布式锁切面
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DistributedLock.class)
    public DistributedLockAspect distributedLockAspect(DistributedLock distributedLock, RedisProperties redisProperties) {
        return new DistributedLockAspect(distributedLock, redisProperties);
    }

    /**
     * 注册限流切面。
     *
     * @param stringRedisTemplate StringRedisTemplate
     * @param redisProperties     Redis 配置
     * @return 限流切面
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimiterAspect rateLimiterAspect(StringRedisTemplate stringRedisTemplate, RedisProperties redisProperties) {
        return new RateLimiterAspect(
                stringRedisTemplate,
                redisProperties,
                new ResourceScriptSource(new ClassPathResource("scripts/rate_limiter.lua"))
        );
    }

    /**
     * 注册幂等切面。
     *
     * @param stringRedisTemplate StringRedisTemplate
     * @param redisProperties     Redis 配置
     * @return 幂等切面
     */
    @Bean
    @ConditionalOnMissingBean(type = "com.sloth.boot.starter.idempotent.aspect.IdempotentAspect")
    public IdempotentAspect idempotentAspect(StringRedisTemplate stringRedisTemplate, RedisProperties redisProperties) {
        return new IdempotentAspect(stringRedisTemplate, redisProperties);
    }

    /**
     * 注册延迟队列工具类。
     *
     * @param redissonClient Redisson 客户端
     * @return 延迟队列工具类
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedissonClient.class)
    public RedisDelayQueue redisDelayQueue(RedissonClient redissonClient) {
        return new RedisDelayQueue(redissonClient);
    }

    /**
     * 注册 Redis 健康检查。
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return 健康检查器
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothRedisHealthIndicator")
    @ConditionalOnClass(HealthIndicator.class)
    public RedisHealthIndicator slothRedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        return new RedisHealthIndicator(redisConnectionFactory);
    }
}
