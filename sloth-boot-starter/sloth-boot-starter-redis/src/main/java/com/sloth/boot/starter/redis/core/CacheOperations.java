package com.sloth.boot.starter.redis.core;

import org.springframework.data.redis.core.RedisCallback;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis 缓存操作接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface CacheOperations {

    /**
     * 设置缓存。
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    void set(String key, Object value);

    /**
     * 设置带过期时间的缓存。
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param timeout 过期时间
     */
    void set(String key, Object value, Duration timeout);

    /**
     * 获取缓存并转换为指定类型。
     *
     * @param key   缓存键
     * @param clazz 目标类型
     * @param <T>   类型参数
     * @return 缓存值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存。
     *
     * @param key 缓存键
     * @return 是否删除成功
     */
    boolean delete(String key);

    /**
     * 批量删除缓存。
     *
     * @param keys 缓存键集合
     * @return 删除数量
     */
    long delete(Collection<String> keys);

    /**
     * 判断键是否存在。
     *
     * @param key 缓存键
     * @return 是否存在
     */
    boolean hasKey(String key);

    /**
     * 设置过期时间。
     *
     * @param key     缓存键
     * @param timeout 过期时间
     * @return 是否设置成功
     */
    boolean expire(String key, Duration timeout);

    /**
     * 获取过期时间，单位秒。
     *
     * @param key 缓存键
     * @return 过期秒数
     */
    Long getExpire(String key);

    /**
     * 自增。
     *
     * @param key   缓存键
     * @param delta 增量
     * @return 自增后值
     */
    Long increment(String key, long delta);

    /**
     * 自减。
     *
     * @param key   缓存键
     * @param delta 减量
     * @return 自减后值
     */
    Long decrement(String key, long delta);

    /**
     * 设置 Hash 字段。
     *
     * @param key   缓存键
     * @param field 字段
     * @param value 值
     */
    void hSet(String key, String field, Object value);

    /**
     * 获取 Hash 字段。
     *
     * @param key   缓存键
     * @param field 字段
     * @param clazz 目标类型
     * @param <T>   类型参数
     * @return 字段值
     */
    <T> T hGet(String key, String field, Class<T> clazz);

    /**
     * 获取整个 Hash。
     *
     * @param key 缓存键
     * @return Hash 内容
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 删除 Hash 字段。
     *
     * @param key    缓存键
     * @param fields 字段数组
     * @return 删除数量
     */
    Long hDel(String key, Object... fields);

    /**
     * 左侧压入 List。
     *
     * @param key   缓存键
     * @param value 值
     * @return 当前长度
     */
    Long lPush(String key, Object value);

    /**
     * 获取 List 范围数据。
     *
     * @param key   缓存键
     * @param start 起始位置
     * @param end   结束位置
     * @return 列表数据
     */
    List<Object> lRange(String key, long start, long end);

    /**
     * 获取 List 长度。
     *
     * @param key 缓存键
     * @return 长度
     */
    Long lLen(String key);

    /**
     * 添加 Set 元素。
     *
     * @param key    缓存键
     * @param values 元素列表
     * @return 添加数量
     */
    Long sAdd(String key, Object... values);

    /**
     * 获取 Set 全量成员。
     *
     * @param key 缓存键
     * @return 成员集合
     */
    Set<Object> sMembers(String key);

    /**
     * 判断 Set 是否包含成员。
     *
     * @param key   缓存键
     * @param value 值
     * @return 是否包含
     */
    boolean sIsMember(String key, Object value);

    /**
     * 添加 ZSet 元素。
     *
     * @param key   缓存键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    boolean zAdd(String key, Object value, double score);

    /**
     * 获取 ZSet 区间数据。
     *
     * @param key   缓存键
     * @param start 起始位置
     * @param end   结束位置
     * @return 结果集
     */
    Set<Object> zRange(String key, long start, long end);

    /**
     * 按分数范围获取 ZSet 数据。
     *
     * @param key 缓存键
     * @param min 最小分数
     * @param max 最大分数
     * @return 结果集
     */
    Set<Object> zRangeByScore(String key, double min, double max);

    /**
     * 获取 ZSet 排名。
     *
     * @param key   缓存键
     * @param value 值
     * @return 排名
     */
    Long zRank(String key, Object value);

    /**
     * 执行 pipeline 批量操作。
     *
     * @param redisCallback Redis 回调
     * @return pipeline 结果
     */
    List<Object> executePipelined(RedisCallback<?> redisCallback);

    /**
     * 基于前缀扫描键。
     *
     * @param pattern 键模式
     * @return 键集合
     */
    Set<String> scan(String pattern);
}
