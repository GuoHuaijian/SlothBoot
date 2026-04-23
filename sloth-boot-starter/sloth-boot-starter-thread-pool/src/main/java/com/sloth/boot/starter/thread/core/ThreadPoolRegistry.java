package com.sloth.boot.starter.thread.core;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程池注册表。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class ThreadPoolRegistry {

    private final Map<String, VisibleThreadPoolExecutor> pools = new ConcurrentHashMap<>();

    /**
     * 注册线程池。
     *
     * @param name     线程池名称
     * @param executor 线程池实例
     */
    public void register(String name, VisibleThreadPoolExecutor executor) {
        pools.put(name, executor);
    }

    /**
     * 获取线程池。
     *
     * @param name 名称
     * @return 线程池
     */
    public VisibleThreadPoolExecutor getPool(String name) {
        return pools.get(name);
    }

    /**
     * 获取所有线程池。
     *
     * @return 线程池映射
     */
    public Map<String, VisibleThreadPoolExecutor> getAllPools() {
        return Collections.unmodifiableMap(pools);
    }

    /**
     * 获取全部线程池运行时信息。
     *
     * @return 运行时信息
     */
    public Map<String, Map<String, Object>> getAllSnapshots() {
        Map<String, Map<String, Object>> result = new ConcurrentHashMap<>();
        pools.forEach((name, executor) -> result.put(name, executor.snapshot()));
        return result;
    }
}
