package com.sloth.boot.starter.threadpool.core;

import com.sloth.boot.starter.threadpool.config.ThreadPoolProperties;
import com.sloth.boot.starter.threadpool.reject.LogRejectedExecutionHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池构建工厂。
 * <p>
 * 提供统一的 {@link VisibleThreadPoolExecutor} 和 {@link ScheduledThreadPoolExecutor} 构建能力，
 * 确保所有线程池都具备可观测性（指标采集、上下文传递、拒绝计数等）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class ThreadPoolBuilderFactory {

    /**
     * 构建可观测线程池。
     *
     * @param poolName   线程池名称
     * @param poolConfig 线程池配置
     * @return 可观测线程池执行器
     */
    public VisibleThreadPoolExecutor buildExecutor(String poolName, ThreadPoolProperties.PoolConfig poolConfig) {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(poolConfig.getQueueCapacity());
        LogRejectedExecutionHandler rejectedHandler = new LogRejectedExecutionHandler(
            poolName,
            "CALLER_RUNS".equalsIgnoreCase(poolConfig.getRejectedPolicy())
        );
        return new VisibleThreadPoolExecutor(
            poolName,
            poolConfig.getCoreSize(),
            poolConfig.getMaxSize(),
            poolConfig.getKeepAliveTime(),
            TimeUnit.SECONDS,
            queue,
            buildThreadFactory(poolConfig.getThreadNamePrefix()),
            (runnable, executor) -> {
                if (executor instanceof VisibleThreadPoolExecutor visibleThreadPoolExecutor) {
                    visibleThreadPoolExecutor.incrementRejectedCount();
                }
                rejectedHandler.rejectedExecution(runnable, executor);
            }
        );
    }

    /**
     * 构建可观测定时任务线程池。
     *
     * @param poolName   线程池名称
     * @param poolConfig 线程池配置
     * @return 可观测定时任务线程池
     */
    public ScheduledThreadPoolExecutor buildScheduledExecutor(String poolName, ThreadPoolProperties.PoolConfig poolConfig) {
        return new ScheduledThreadPoolExecutor(
            poolConfig.getCoreSize(),
            buildThreadFactory(poolConfig.getThreadNamePrefix()),
            new LogRejectedExecutionHandler(poolName, true)
        );
    }

    /**
     * 构建守护线程工厂。
     *
     * @param prefix 线程名前缀
     * @return 线程工厂
     */
    public ThreadFactory buildThreadFactory(String prefix) {
        AtomicInteger counter = new AtomicInteger(1);
        return runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(prefix + counter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        };
    }
}
