package com.sloth.boot.starter.redis.core;

import tools.jackson.databind.ObjectMapper;
import com.sloth.boot.starter.redis.config.RedisProperties;

import java.nio.charset.StandardCharsets;

/**
 * Redis 缓存键和值处理工具类。
 * <p>
 * 提供统一的 key 前缀处理和值类型转换逻辑，供 {@link RedisCacheUtil} 和 {@link RedisCacheStrategy} 共用。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
final class RedisKeyUtil {

    private RedisKeyUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    static String buildKey(String key, RedisProperties properties) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Redis key must not be null or blank");
        }
        String prefix = properties.getKeyPrefix();
        if (prefix != null && !prefix.isEmpty() && !key.startsWith(prefix)) {
            return prefix + key;
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    static <T> T castValue(Object value, Class<T> clazz, ObjectMapper objectMapper) {
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        if (value instanceof byte[] bytes) {
            return objectMapper.convertValue(new String(bytes, StandardCharsets.UTF_8), clazz);
        }
        return objectMapper.convertValue(value, clazz);
    }
}
