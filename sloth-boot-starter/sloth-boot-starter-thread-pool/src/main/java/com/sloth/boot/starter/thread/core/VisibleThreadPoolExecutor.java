package com.sloth.boot.starter.thread.core;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.context.UserContext;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

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
        return Map.of(
                "poolName", poolName,
                "corePoolSize", getCorePoolSize(),
                "maximumPoolSize", getMaximumPoolSize(),
                "poolSize", getPoolSize(),
                "activeCount", getActiveCount(),
                "completedTaskCount", getCompletedTaskCount(),
                "taskCount", getTaskCount(),
                "queueSize", getQueueSize(),
                "queueRemainingCapacity", getQueueRemainingCapacity(),
                "rejectedCount", rejectedCount.get(),
                "maxCostTime", maxCostTime.get(),
                "avgCostTime", getAvgCostTime()
        );
    }

    private Runnable wrap(Runnable runnable) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        UserContext.UserInfo userInfo = UserContext.get();
        TraceContext.TraceInfo traceInfo = TraceContext.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return () -> {
            Map<String, String> oldMdc = MDC.getCopyOfContextMap();
            UserContext.UserInfo oldUser = UserContext.get();
            TraceContext.TraceInfo oldTrace = TraceContext.get();
            RequestAttributes oldRequestAttributes = RequestContextHolder.getRequestAttributes();
            try {
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                } else {
                    MDC.clear();
                }
                if (userInfo != null) {
                    UserContext.set(userInfo);
                }
                if (traceInfo != null) {
                    TraceContext.set(traceInfo);
                }
                if (requestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                }
                runnable.run();
            } finally {
                if (oldMdc != null) {
                    MDC.setContextMap(oldMdc);
                } else {
                    MDC.clear();
                }
                if (oldUser != null) {
                    UserContext.set(oldUser);
                } else {
                    UserContext.clear();
                }
                if (oldTrace != null) {
                    TraceContext.set(oldTrace);
                } else {
                    TraceContext.clear();
                }
                if (oldRequestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(oldRequestAttributes);
                } else {
                    RequestContextHolder.resetRequestAttributes();
                }
            }
        };
    }

    private <T> Callable<T> wrap(Callable<T> callable) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        UserContext.UserInfo userInfo = UserContext.get();
        TraceContext.TraceInfo traceInfo = TraceContext.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return () -> {
            Map<String, String> oldMdc = MDC.getCopyOfContextMap();
            UserContext.UserInfo oldUser = UserContext.get();
            TraceContext.TraceInfo oldTrace = TraceContext.get();
            RequestAttributes oldRequestAttributes = RequestContextHolder.getRequestAttributes();
            try {
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                } else {
                    MDC.clear();
                }
                if (userInfo != null) {
                    UserContext.set(userInfo);
                }
                if (traceInfo != null) {
                    TraceContext.set(traceInfo);
                }
                if (requestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                }
                return callable.call();
            } finally {
                if (oldMdc != null) {
                    MDC.setContextMap(oldMdc);
                } else {
                    MDC.clear();
                }
                if (oldUser != null) {
                    UserContext.set(oldUser);
                } else {
                    UserContext.clear();
                }
                if (oldTrace != null) {
                    TraceContext.set(oldTrace);
                } else {
                    TraceContext.clear();
                }
                if (oldRequestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(oldRequestAttributes);
                } else {
                    RequestContextHolder.resetRequestAttributes();
                }
            }
        };
    }
}
