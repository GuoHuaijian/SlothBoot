package com.sloth.boot.starter.redis.config;

import tools.jackson.databind.ObjectMapper;
import com.sloth.boot.starter.redis.bloom.RedisBloomFilter;
import com.sloth.boot.starter.redis.cache.MultiLevelCacheManager;
import com.sloth.boot.starter.redis.core.RedisCacheUtil;
import com.sloth.boot.starter.redis.delay.RedisDelayQueue;
import com.sloth.boot.starter.redis.id.RedisIdGenerator;

import com.sloth.boot.starter.redis.limiter.RateLimiterAspect;
import com.sloth.boot.starter.redis.lock.DistributedLock;
import com.sloth.boot.starter.redis.lock.DistributedLockAspect;
import com.sloth.boot.starter.redis.lock.DistributedReadWriteLock;
import com.sloth.boot.starter.redis.lock.RedissonDistributedLock;
import com.sloth.boot.starter.redis.lock.RedissonReadWriteLock;
import com.sloth.boot.starter.redis.pubsub.RedisPubSubTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
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

/**
 * Redis 自动配置。
 * <p>
 * 注册 {@link RedisTemplate}、{@link StringRedisTemplate}、{@link RedisCacheUtil}、
 * {@link DistributedLock}、{@link DistributedLockAspect}、{@link RateLimiterAspect}、
 * {@link RedisDelayQueue}、{@link RedisIdGenerator}、{@link DistributedReadWriteLock}、
 * {@link MultiLevelCacheManager}、{@link RedisBloomFilter}、{@link RedisPubSubTemplate}，
 * 支持条件装配。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration(after = org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration.class)
@ConditionalOnClass({RedisTemplate.class, StringRedisTemplate.class})
@ConditionalOnProperty(prefix = "sloth.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RedisProperties.class)
@Import(RedisSerializerConfig.class)
public class RedisAutoConfiguration {

    /**
     * 注册 RedisTemplate。
     *
     * @param redisConnectionFactory             Redis 连接工厂
     * @param genericJackson2JsonRedisSerializer JSON 序列化器
     * @return RedisTemplate
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "slothRedisTemplate")
    public RedisTemplate<String, Object> slothRedisTemplate(RedisConnectionFactory redisConnectionFactory,
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
    public DistributedLockAspect distributedLockAspect(DistributedLock distributedLock,
                                                       RedisProperties redisProperties) {
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
    public RateLimiterAspect rateLimiterAspect(StringRedisTemplate stringRedisTemplate,
                                               RedisProperties redisProperties) {
        return new RateLimiterAspect(stringRedisTemplate, redisProperties,
            new ResourceScriptSource(new ClassPathResource("scripts/rate_limiter.lua")));
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
     * 注册分布式 ID 生成器。
     *
     * @param stringRedisTemplate StringRedisTemplate
     * @param redisProperties     Redis 配置
     * @return 分布式 ID 生成器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.redis.id-generator", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RedisIdGenerator redisIdGenerator(StringRedisTemplate stringRedisTemplate, RedisProperties redisProperties) {
        return new RedisIdGenerator(stringRedisTemplate, redisProperties,
            new ResourceScriptSource(new ClassPathResource("scripts/id_generator.lua")));
    }

    /**
     * 注册分布式读写锁实现。
     *
     * @param redissonClient Redisson 客户端
     * @return 分布式读写锁
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedissonClient.class)
    public DistributedReadWriteLock distributedReadWriteLock(RedissonClient redissonClient) {
        return new RedissonReadWriteLock(redissonClient);
    }

    /**
     * 注册多级缓存管理器（L1 Caffeine + L2 Redis）。
     *
     * @param redisTemplate   RedisTemplate
     * @param redisProperties Redis 配置
     * @return 多级缓存管理器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.redis.multi-cache", name = "enabled", havingValue = "true")
    @ConditionalOnClass(name = "com.github.benmanes.caffeine.cache.Caffeine")
    public MultiLevelCacheManager multiLevelCacheManager(
        @Qualifier("slothRedisTemplate") RedisTemplate<String, Object> redisTemplate, RedisProperties redisProperties) {
        return new MultiLevelCacheManager(redisTemplate, redisProperties);
    }

    /**
     * 注册 Redis 布隆过滤器。
     *
     * @param redissonClient  Redisson 客户端
     * @param redisProperties Redis 配置
     * @return Redis 布隆过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnProperty(prefix = "sloth.redis.bloom", name = "enabled", havingValue = "true")
    public RedisBloomFilter<?> redisBloomFilter(RedissonClient redissonClient, RedisProperties redisProperties) {
        RedisProperties.BloomFilter config = redisProperties.getBloomFilter();
        return new RedisBloomFilter<>(redissonClient, config.getName(), config.getExpectedInsertions(),
            config.getFalsePositiveProbability());
    }

    /**
     * 注册 Redis Pub/Sub 消息模板。
     *
     * @param stringRedisTemplate StringRedisTemplate
     * @param objectMapper        JSON 序列化器
     * @param connectionFactory   Redis 连接工厂
     * @return Redis Pub/Sub 消息模板
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.redis.pubsub", name = "enabled", havingValue = "true")
    public RedisPubSubTemplate redisPubSubTemplate(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper,
                                                   RedisConnectionFactory connectionFactory) {
        return new RedisPubSubTemplate(stringRedisTemplate, objectMapper, connectionFactory);
    }
}
