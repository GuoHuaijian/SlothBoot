package com.sloth.boot.starter.threadpool.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 线程池动态管理器。
 * <p>
 * 支持运行时动态调整线程池参数（核心线程数、最大线程数），
 * 无需重启应用。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class ThreadPoolManager {

    private final ThreadPoolRegistry threadPoolRegistry;

    /**
     * 动态更新线程池的核心线程数和最大线程数。
     *
     * @param poolName 线程池名称
     * @param coreSize 新的核心线程数
     * @param maxSize  新的最大线程数
     */
    public void updatePoolSize(String poolName, int coreSize, int maxSize) {
        VisibleThreadPoolExecutor executor = threadPoolRegistry.getPool(poolName);
        if (executor == null) {
            log.warn("[ThreadPool] 线程池不存在: {}", poolName);
            return;
        }
        log.info("[ThreadPool] 动态调整线程池 {}: coreSize={}, maxSize={}", poolName, coreSize, maxSize);
        executor.setCorePoolSize(coreSize);
        executor.setMaximumPoolSize(maxSize);
    }

    /**
     * 获取线程池快照信息。
     *
     * @param poolName 线程池名称
     * @return 快照信息，不存在时返回 null
     */
    public ThreadPoolSnapshot getSnapshot(String poolName) {
        VisibleThreadPoolExecutor executor = threadPoolRegistry.getPool(poolName);
        if (executor == null) {
            return null;
        }
        return executor.snapshot();
    }
}
