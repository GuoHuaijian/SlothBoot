package com.sloth.boot.starter.redis.cache;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

/**
 * 多级缓存实现（L1 Caffeine + L2 Redis）。
 * <p>
 * 读取：L1 → L2 → 源数据回填双层。
 * 写入：同时写入 L1 和 L2。
 * 清除：同时清除 L1 和 L2。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class MultiLevelCache implements Cache {

    private final String name;
    private final Cache l1Cache;
    private final Cache l2Cache;

    /**
     * 构造多级缓存。
     *
     * @param name    缓存名称
     * @param l1Cache L1 本地缓存
     * @param l2Cache L2 远程缓存
     */
    public MultiLevelCache(String name, Cache l1Cache, Cache l2Cache) {
        this.name = name;
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
    }

    /**
     * 获取缓存名称。
     *
     * @return 缓存名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 获取原生缓存对象。
     *
     * @return 当前实例
     */
    @Override
    public Object getNativeCache() {
        return this;
    }

    /**
     * 获取缓存值，先查 L1，miss 后查 L2，L2 命中后回填 L1。
     *
     * @param key 缓存键
     * @return 缓存值包装器，未命中返回 null
     */
    @Override
    @Nullable
    public ValueWrapper get(Object key) {
        // L1 查询
        ValueWrapper wrapper = l1Cache.get(key);
        if (wrapper != null) {
            return wrapper;
        }
        // L2 查询，命中后回填 L1
        wrapper = l2Cache.get(key);
        if (wrapper != null) {
            l1Cache.put(key, wrapper.get());
        }
        return wrapper;
    }

    /**
     * 获取缓存值并转换为指定类型。
     *
     * @param key  缓存键
     * @param type 目标类型
     * @param <T>  类型参数
     * @return 缓存值，未命中返回 null
     */
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        // L1 查询
        T value = l1Cache.get(key, type);
        if (value != null) {
            return value;
        }
        // L2 查询
        value = l2Cache.get(key, type);
        if (value != null) {
            l1Cache.put(key, value);
        }
        return value;
    }

    /**
     * 获取缓存值，未命中时通过 valueLoader 加载并回填双层缓存。
     *
     * @param key         缓存键
     * @param valueLoader 数据加载函数
     * @param <T>         类型参数
     * @return 缓存值
     */
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        // L1 查询
        T value = l1Cache.get(key, (Class<T>) Object.class);
        if (value != null) {
            return value;
        }
        // L2 查询
        value = l2Cache.get(key, valueLoader);
        if (value != null) {
            l1Cache.put(key, value);
        }
        return value;
    }

    /**
     * 写入缓存，同时写入 L1 和 L2。
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    @Override
    public void put(Object key, @Nullable Object value) {
        l1Cache.put(key, value);
        l2Cache.put(key, value);
    }

    /**
     * 驱逐指定键，同时清除 L1 和 L2。
     *
     * @param key 缓存键
     */
    @Override
    public void evict(Object key) {
        l1Cache.evict(key);
        l2Cache.evict(key);
    }

    /**
     * 清空全部缓存，同时清除 L1 和 L2。
     */
    @Override
    public void clear() {
        l1Cache.clear();
        l2Cache.clear();
    }
}
