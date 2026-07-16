package com.sloth.boot.starter.threadpool.core;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 线程池注册表。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class ThreadPoolRegistry {

    private final Map<String, VisibleThreadPoolExecutor> pools = new ConcurrentHashMap<>();
    private final Map<String, ScheduledThreadPoolExecutor> scheduledPools = new ConcurrentHashMap<>();

    /**
     * 注册可观测线程池。
     *
     * @param name     线程池名称
     * @param executor 线程池实例
     */
    public void register(String name, VisibleThreadPoolExecutor executor) {
        pools.put(name, executor);
    }

    /**
     * 注册定时任务线程池。
     *
     * @param name     线程池名称
     * @param executor 定时线程池实例
     */
    public void registerScheduled(String name, ScheduledThreadPoolExecutor executor) {
        scheduledPools.put(name, executor);
    }

    /**
     * 获取可观测线程池。
     *
     * @param name 名称
     * @return 线程池
     */
    public VisibleThreadPoolExecutor getPool(String name) {
        return pools.get(name);
    }

    /**
     * 获取定时任务线程池。
     *
     * @param name 名称
     * @return 定时线程池
     */
    public ScheduledThreadPoolExecutor getScheduledPool(String name) {
        return scheduledPools.get(name);
    }

    /**
     * 获取所有可观测线程池。
     *
     * @return 线程池映射
     */
    public Map<String, VisibleThreadPoolExecutor> getAllPools() {
        return Collections.unmodifiableMap(pools);
    }

    /**
     * 获取所有定时任务线程池。
     *
     * @return 定时线程池映射
     */
    public Map<String, ScheduledThreadPoolExecutor> getAllScheduledPools() {
        return Collections.unmodifiableMap(scheduledPools);
    }

    /**
     * 获取全部线程池运行时快照（含可观测线程池和定时任务线程池）。
     *
     * @return 运行时快照
     */
    public Map<String, ThreadPoolSnapshot> getAllSnapshots() {
        Map<String, ThreadPoolSnapshot> result = new ConcurrentHashMap<>();
        pools.forEach((name, executor) -> result.put(name, executor.snapshot()));
        scheduledPools.forEach((name, executor) -> result.put(name, scheduledSnapshot(name, executor)));
        return result;
    }

    private ThreadPoolSnapshot scheduledSnapshot(String name, ScheduledThreadPoolExecutor executor) {
        return new ThreadPoolSnapshot(
            name,
            executor.getCorePoolSize(),
            executor.getMaximumPoolSize(),
            executor.getPoolSize(),
            executor.getActiveCount(),
            executor.getCompletedTaskCount(),
            executor.getTaskCount(),
            executor.getQueue().size(),
            executor.getQueue().remainingCapacity(),
            0L,
            0L,
            0L
        );
    }
}
