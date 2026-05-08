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

    public MultiLevelCache(String name, Cache l1Cache, Cache l2Cache) {
        this.name = name;
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

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

    @Override
    public void put(Object key, @Nullable Object value) {
        l1Cache.put(key, value);
        l2Cache.put(key, value);
    }

    @Override
    public void evict(Object key) {
        l1Cache.evict(key);
        l2Cache.evict(key);
    }

    @Override
    public void clear() {
        l1Cache.clear();
        l2Cache.clear();
    }
}
