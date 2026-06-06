package com.sloth.boot.starter.redis.core;

import cn.hutool.core.util.StrUtil;
import tools.jackson.databind.ObjectMapper;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.redis.config.RedisProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

/**
 * Redis 缓存策略类，提供高级缓存模式。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class RedisCacheStrategy {

    private static final Object NULL_HOLDER = "__NULL__";
    private static final ObjectMapper OBJECT_MAPPER = JsonUtil.getObjectMapper();

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    /**
     * 获取缓存，未命中时加载并回填，带空值缓存保护。
     *
     * @param key      缓存键
     * @param clazz    目标类型
     * @param supplier 加载函数
     * @param timeout  过期时间
     * @param <T>      类型参数
     * @return 结果值
     */
    public <T> T getOrLoad(String key, Class<T> clazz, Supplier<T> supplier, Duration timeout) {
        String redisKey = buildKey(key);
        Object cached = redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            if (NULL_HOLDER.equals(cached)) {
                return null;
            }
            return castValue(cached, clazz);
        }
        T loaded = supplier.get();
        if (loaded == null) {
            redisTemplate.opsForValue().set(redisKey, NULL_HOLDER,
                Duration.ofSeconds(redisProperties.getNullValueExpireSeconds()));
            return null;
        }
        redisTemplate.opsForValue().set(redisKey, loaded, timeout);
        return loaded;
    }

    /**
     * 获取逻辑过期缓存，过期时触发异步重建。
     *
     * @param key      缓存键
     * @param clazz    目标类型
     * @param supplier 数据加载函数
     * @param timeout  逻辑过期时间
     * @param <T>      类型参数
     * @return 缓存值
     */
    public <T> T getWithLogicalExpire(String key, Class<T> clazz, Supplier<T> supplier, Duration timeout) {
        String redisKey = buildKey(key);
        Object cached = redisTemplate.opsForValue().get(redisKey);
        if (cached == null) {
            T loaded = supplier.get();
            if (loaded == null) {
                return null;
            }
            RedisCacheData cacheData = new RedisCacheData();
            cacheData.setData(loaded);
            cacheData.setExpireTime(LocalDateTime.now().plusSeconds(timeout.toSeconds()));
            redisTemplate.opsForValue().set(redisKey, cacheData, timeout.plusMinutes(5));
            return loaded;
        }
        RedisCacheData cacheData = OBJECT_MAPPER.convertValue(cached, RedisCacheData.class);
        T value = castValue(cacheData.getData(), clazz);
        if (cacheData.getExpireTime() != null && cacheData.getExpireTime().isAfter(LocalDateTime.now())) {
            return value;
        }
        String rebuildLockKey = redisKey + ":logical:rebuild";
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(rebuildLockKey, "1", Duration.ofSeconds(30));
        if (Boolean.TRUE.equals(locked)) {
            try {
                T refreshed = supplier.get();
                RedisCacheData refreshedData = new RedisCacheData();
                refreshedData.setData(refreshed);
                refreshedData.setExpireTime(LocalDateTime.now().plusSeconds(timeout.toSeconds()));
                redisTemplate.opsForValue().set(redisKey, refreshedData, timeout.plusMinutes(5));
                value = refreshed;
            } finally {
                redisTemplate.delete(rebuildLockKey);
            }
        }
        return value;
    }

    private String buildKey(String key) {
        if (StrUtil.isBlank(key)) {
            return redisProperties.getKeyPrefix();
        }
        return key.startsWith(redisProperties.getKeyPrefix()) ? key : redisProperties.getKeyPrefix() + key;
    }

    private <T> T castValue(Object value, Class<T> clazz) {
        if (value == null || clazz == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        if (value instanceof String str && NULL_HOLDER.equals(str)) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(value, clazz);
    }

    /**
     * 逻辑过期缓存包装对象。
     *
     * @author sloth-boot
     * @since 1.0.0
     */
    @Data
    public static class RedisCacheData implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 业务数据。
         */
        private Object data;

        /**
         * 逻辑过期时间。
         */
        private LocalDateTime expireTime;
    }
}
