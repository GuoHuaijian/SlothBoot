package com.sloth.boot.starter.thread.reject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 拒绝策略日志处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class LogRejectedExecutionHandler implements RejectedExecutionHandler {

    private final String poolName;
    private final boolean callerRunsFallback;

    /**
     * 处理被拒绝的任务。
     *
     * @param runnable 任务
     * @param executor 线程池
     */
    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        log.warn("线程池任务被拒绝, poolName={}, activeCount={}, poolSize={}, queueSize={}, queueRemainingCapacity={}, task={}",
                poolName,
                executor.getActiveCount(),
                executor.getPoolSize(),
                executor.getQueue().size(),
                executor.getQueue().remainingCapacity(),
                runnable);
        if (callerRunsFallback && !executor.isShutdown()) {
            runnable.run();
        }
    }
}
