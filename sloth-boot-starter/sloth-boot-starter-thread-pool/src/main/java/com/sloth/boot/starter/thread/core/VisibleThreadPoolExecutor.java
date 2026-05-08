package com.sloth.boot.starter.thread.core;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.sloth.boot.common.util.ContextSnapshot;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 可观测线程池执行器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class VisibleThreadPoolExecutor extends ThreadPoolExecutor {

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    private final String poolName;
    private final AtomicLong rejectedCount = new AtomicLong();
    private final AtomicLong totalCostTime = new AtomicLong();
    private final AtomicLong executeCount = new AtomicLong();
    private final AtomicLong maxCostTime = new AtomicLong();

    /**
     * 构造函数。
     *
     * @param poolName              线程池名称
     * @param corePoolSize          核心线程数
     * @param maximumPoolSize       最大线程数
     * @param keepAliveTime         空闲线程存活时间
     * @param unit                  时间单位
     * @param workQueue             任务队列
     * @param threadFactory         线程工厂
     * @param rejectedExecutionHandler 拒绝策略
     */
    public VisibleThreadPoolExecutor(String poolName,
                                     int corePoolSize,
                                     int maximumPoolSize,
                                     long keepAliveTime,
                                     TimeUnit unit,
                                     BlockingQueue<Runnable> workQueue,
                                     ThreadFactory threadFactory,
                                     RejectedExecutionHandler rejectedExecutionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectedExecutionHandler);
        this.poolName = poolName;
    }

    /**
     * 执行前记录开始时间。
     *
     * @param thread   当前线程
     * @param runnable 任务
     */
    @Override
    protected void beforeExecute(Thread thread, Runnable runnable) {
        START_TIME.set(System.currentTimeMillis());
        super.beforeExecute(thread, runnable);
    }

    /**
     * 执行后统计耗时。
     *
     * @param runnable 任务
     * @param throwable 异常
     */
    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        Long start = START_TIME.get();
        if (start != null) {
            long cost = System.currentTimeMillis() - start;
            totalCostTime.addAndGet(cost);
            executeCount.incrementAndGet();
            maxCostTime.updateAndGet(current -> Math.max(current, cost));
        }
        START_TIME.remove();
        super.afterExecute(runnable, throwable);
    }

    /**
     * 包装后执行任务。
     *
     * @param command 命令
     */
    @Override
    public void execute(Runnable command) {
        super.execute(TtlRunnable.get(wrap(command)));
    }

    /**
     * 提交 Runnable 任务。
     *
     * @param task 任务
     * @return Future
     */
    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(TtlRunnable.get(wrap(task)));
    }

    /**
     * 提交 Callable 任务。
     *
     * @param task 任务
     * @param <T>  返回值类型
     * @return Future
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(TtlCallable.get(wrap(task)));
    }

    /**
     * 记录拒绝次数。
     */
    public void incrementRejectedCount() {
        rejectedCount.incrementAndGet();
    }

    /**
     * 获取平均耗时。
     *
     * @return 平均耗时
     */
    public long getAvgCostTime() {
        long executed = executeCount.get();
        return executed == 0 ? 0L : totalCostTime.get() / executed;
    }

    /**
     * 获取队列大小。
     *
     * @return 队列大小
     */
    public int getQueueSize() {
        return getQueue().size();
    }

    /**
     * 获取队列剩余容量。
     *
     * @return 剩余容量
     */
    public int getQueueRemainingCapacity() {
        return getQueue().remainingCapacity();
    }

    /**
     * 获取线程池快照。
     *
     * @return 线程池运行状态
     */
    public Map<String, Object> snapshot() {
        Map<String, Object> snapshot = new LinkedHashMap<>(16);
        snapshot.put("poolName", poolName);
        snapshot.put("corePoolSize", getCorePoolSize());
        snapshot.put("maximumPoolSize", getMaximumPoolSize());
        snapshot.put("poolSize", getPoolSize());
        snapshot.put("activeCount", getActiveCount());
        snapshot.put("completedTaskCount", getCompletedTaskCount());
        snapshot.put("taskCount", getTaskCount());
        snapshot.put("queueSize", getQueueSize());
        snapshot.put("queueRemainingCapacity", getQueueRemainingCapacity());
        snapshot.put("rejectedCount", rejectedCount.get());
        snapshot.put("maxCostTime", maxCostTime.get());
        snapshot.put("avgCostTime", getAvgCostTime());
        return snapshot;
    }

    private Runnable wrap(Runnable runnable) {
        ContextSnapshot snapshot = ContextSnapshot.capture();
        return snapshot.decorate(runnable);
    }

    private <T> Callable<T> wrap(Callable<T> callable) {
        ContextSnapshot snapshot = ContextSnapshot.capture();
        return snapshot.decorate(callable);
    }
}
