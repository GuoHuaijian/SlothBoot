package com.sloth.boot.starter.thread.monitor;

import com.sloth.boot.starter.thread.core.ThreadPoolRegistry;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * 线程池监控指标注册器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class ThreadPoolMetrics {

    /**
     * 注册线程池监控指标。
     *
     * @param meterRegistry      指标注册中心
     * @param threadPoolRegistry 线程池注册表
     */
    public ThreadPoolMetrics(MeterRegistry meterRegistry, ThreadPoolRegistry threadPoolRegistry) {
        threadPoolRegistry.getAllPools().forEach((name, executor) -> {
            Gauge.builder("thread.pool." + name + ".active", executor, pool -> (double) pool.getActiveCount())
                    .register(meterRegistry);
            Gauge.builder("thread.pool." + name + ".queue.size", executor, pool -> (double) pool.getQueueSize())
                    .register(meterRegistry);
            Gauge.builder("thread.pool." + name + ".completed", executor, pool -> (double) pool.getCompletedTaskCount())
                    .register(meterRegistry);
            Gauge.builder("thread.pool." + name + ".rejected", executor, pool -> (double) pool.getRejectedCount().get())
                    .register(meterRegistry);
        });
    }
}
