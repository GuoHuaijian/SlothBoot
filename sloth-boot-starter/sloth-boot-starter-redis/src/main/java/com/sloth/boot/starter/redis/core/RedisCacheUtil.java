package com.sloth.boot.starter.redis.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.redis.config.RedisProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 缓存工具类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class RedisCacheUtil {

    private static final Object NULL_HOLDER = "__NULL__";
    private static final ObjectMapper OBJECT_MAPPER = JsonUtil.getObjectMapper().copy();

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    /**
     * 设置缓存。
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(buildKey(key), value);
    }

    /**
     * 设置带过期时间的缓存。
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param timeout 过期时间
     */
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(buildKey(key), value, timeout);
    }

    /**
     * 获取缓存并转换为指定类型。
     *
     * @param key   缓存键
     * @param clazz 目标类型
     * @param <T>   类型参数
     * @return 缓存值
     */
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(buildKey(key));
        return castValue(value, clazz);
    }

    /**
     * 删除缓存。
     *
     * @param key 缓存键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        Boolean deleted = redisTemplate.delete(buildKey(key));
        return Boolean.TRUE.equals(deleted);
    }

    /**
     * 批量删除缓存。
     *
     * @param keys 缓存键集合
     * @return 删除数量
     */
    public long delete(Collection<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return 0L;
        }
        Long deleted = redisTemplate.delete(keys.stream().filter(Objects::nonNull).map(this::buildKey).toList());
        return deleted == null ? 0L : deleted;
    }

    /**
     * 判断键是否存在。
     *
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        Boolean exists = redisTemplate.hasKey(buildKey(key));
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 设置过期时间。
     *
     * @param key     缓存键
     * @param timeout 过期时间
     * @return 是否设置成功
     */
    public boolean expire(String key, Duration timeout) {
        Boolean result = redisTemplate.expire(buildKey(key), timeout);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 获取过期时间，单位秒。
     *
     * @param key 缓存键
     * @return 过期秒数
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(buildKey(key), TimeUnit.SECONDS);
    }

    /**
     * 自增。
     *
     * @param key   缓存键
     * @param delta 增量
     * @return 自增后值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(buildKey(key), delta);
    }

    /**
     * 自减。
     *
     * @param key   缓存键
     * @param delta 减量
     * @return 自减后值
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().increment(buildKey(key), -delta);
    }

    /**
     * 设置 Hash 字段。
     *
     * @param key   缓存键
     * @param field 字段
     * @param value 值
     */
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(buildKey(key), field, value);
    }

    /**
     * 获取 Hash 字段。
     *
     * @param key   缓存键
     * @param field 字段
     * @param clazz 目标类型
     * @param <T>   类型参数
     * @return 字段值
     */
    public <T> T hGet(String key, String field, Class<T> clazz) {
        Object value = redisTemplate.opsForHash().get(buildKey(key), field);
        return castValue(value, clazz);
    }

    /**
     * 获取整个 Hash。
     *
     * @param key 缓存键
     * @return Hash 内容
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(buildKey(key));
    }

    /**
     * 删除 Hash 字段。
     *
     * @param key    缓存键
     * @param fields 字段数组
     * @return 删除数量
     */
    public Long hDel(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(buildKey(key), fields);
    }

    /**
     * 左侧压入 List。
     *
     * @param key   缓存键
     * @param value 值
     * @return 当前长度
     */
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(buildKey(key), value);
    }

    /**
     * 获取 List 范围数据。
     *
     * @param key   缓存键
     * @param start 起始位置
     * @param end   结束位置
     * @return 列表数据
     */
    public List<Object> lRange(String key, long start, long end) {
        List<Object> list = redisTemplate.opsForList().range(buildKey(key), start, end);
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 获取 List 长度。
     *
     * @param key 缓存键
     * @return 长度
     */
    public Long lLen(String key) {
        return redisTemplate.opsForList().size(buildKey(key));
    }

    /**
     * 添加 Set 元素。
     *
     * @param key    缓存键
     * @param values 元素列表
     * @return 添加数量
     */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(buildKey(key), values);
    }

    /**
     * 获取 Set 全量成员。
     *
     * @param key 缓存键
     * @return 成员集合
     */
    public Set<Object> sMembers(String key) {
        Set<Object> members = redisTemplate.opsForSet().members(buildKey(key));
        return members == null ? Collections.emptySet() : members;
    }

    /**
     * 判断 Set 是否包含成员。
     *
     * @param key   缓存键
     * @param value 值
     * @return 是否包含
     */
    public boolean sIsMember(String key, Object value) {
        Boolean result = redisTemplate.opsForSet().isMember(buildKey(key), value);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 添加 ZSet 元素。
     *
     * @param key   缓存键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public boolean zAdd(String key, Object value, double score) {
        Boolean result = redisTemplate.opsForZSet().add(buildKey(key), value, score);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 获取 ZSet 区间数据。
     *
     * @param key   缓存键
     * @param start 起始位置
     * @param end   结束位置
     * @return 结果集
     */
    public Set<Object> zRange(String key, long start, long end) {
        Set<Object> values = redisTemplate.opsForZSet().range(buildKey(key), start, end);
        return values == null ? Collections.emptySet() : values;
    }

    /**
     * 按分数范围获取 ZSet 数据。
     *
     * @param key 缓存键
     * @param min 最小分数
     * @param max 最大分数
     * @return 结果集
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        Set<Object> values = redisTemplate.opsForZSet().rangeByScore(buildKey(key), min, max);
        return values == null ? Collections.emptySet() : values;
    }

    /**
     * 获取 ZSet 排名。
     *
     * @param key   缓存键
     * @param value 值
     * @return 排名
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(buildKey(key), value);
    }

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
            redisTemplate.opsForValue().set(
                    redisKey,
                    NULL_HOLDER,
                    Duration.ofSeconds(redisProperties.getNullValueExpireSeconds())
            );
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

    /**
     * 执行 pipeline 批量操作。
     *
     * @param redisCallback Redis 回调
     * @return pipeline 结果
     */
    public List<Object> executePipelined(RedisCallback<?> redisCallback) {
        return redisTemplate.executePipelined(redisCallback);
    }

    /**
     * 基于前缀扫描键。
     *
     * @param pattern 键模式
     * @return 键集合
     */
    public Set<String> scan(String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = CollUtil.newHashSet();
            try (var cursor = connection.scan(ScanOptions.scanOptions().match(buildKey(pattern)).count(500).build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            }
            return keys;
        });
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
