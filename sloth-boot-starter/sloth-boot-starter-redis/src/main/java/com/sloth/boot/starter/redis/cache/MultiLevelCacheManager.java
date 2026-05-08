package com.sloth.boot.starter.redis.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.sloth.boot.starter.redis.config.RedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存管理器（L1 Caffeine + L2 Redis）。
 * <p>
 * 读取策略：先查 L1，miss 后查 L2，L2 hit 回填 L1。
 * 写入策略：同时写入 L1 和 L2（write-through）。
 * 驱逐策略：同时清除 L1 和 L2。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class MultiLevelCacheManager implements org.springframework.cache.CacheManager {

    private final CaffeineCacheManager caffeineCacheManager;
    private final RedisCacheManager redisCacheManager;
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    /**
     * 构造多级缓存管理器。
     *
     * @param redisTemplate   Redis 模板
     * @param redisProperties Redis 配置
     */
    public MultiLevelCacheManager(RedisTemplate<String, Object> redisTemplate, RedisProperties redisProperties) {
        RedisProperties.MultiCache config = redisProperties.getMultiCache();

        // L1: Caffeine
        this.caffeineCacheManager = new CaffeineCacheManager();
        this.caffeineCacheManager.setCaffeine(Caffeine.newBuilder().maximumSize(config.getL1MaxSize())
            .expireAfterWrite(config.getL1TtlSeconds(), TimeUnit.SECONDS));

        // L2: Redis
        this.redisCacheManager = RedisCacheManager.builder(redisTemplate.getConnectionFactory())
            .cacheDefaults(org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith(redisProperties.getKeyPrefix() + "cache:"))
            .build();
    }

    /**
     * 获取多级缓存实例，不存在时自动创建。
     *
     * @param name 缓存名称
     * @return 多级缓存实例
     */
    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, n -> {
            Cache l1 = caffeineCacheManager.getCache(n);
            Cache l2 = redisCacheManager.getCache(n);
            return new MultiLevelCache(n, l1, l2);
        });
    }

    /**
     * 获取所有缓存名称。
     *
     * @return 缓存名称集合
     */
    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }
}
