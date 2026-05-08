package com.sloth.boot.starter.monitor.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 业务指标工具类。
 * <p>
 * 简化常见业务指标的记录，提供 counter / timer / gauge 便捷方法。
 * <pre>
 * &#64;Autowired
 * private BusinessMetrics metrics;
 *
 * // 计数器
 * metrics.increment("order.created");
 *
 * // 计时器
 * metrics.record("order.process", 150, TimeUnit.MILLISECONDS);
 *
 * // 包装执行并计时
 * String result = metrics.timer("order.process", () -> orderService.process());
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class BusinessMetrics {

    private final MeterRegistry meterRegistry;

    /**
     * 递增计数器。
     *
     * @param name 指标名称
     */
    public void increment(String name) {
        Counter.builder(name).register(meterRegistry).increment();
    }

    /**
     * 递增计数器（带标签）。
     *
     * @param name 指标名称
     * @param tags 标签键值对（key1, value1, key2, value2, ...）
     */
    public void increment(String name, String... tags) {
        Counter.builder(name).tags(tags).register(meterRegistry).increment();
    }

    /**
     * 递增计数器（指定增量）。
     *
     * @param name  指标名称
     * @param count 增量
     */
    public void increment(String name, double count) {
        Counter.builder(name).register(meterRegistry).increment(count);
    }

    /**
     * 记录耗时。
     *
     * @param name     指标名称
     * @param duration 耗时
     * @param unit     时间单位
     */
    public void record(String name, long duration, TimeUnit unit) {
        Timer.builder(name).register(meterRegistry).record(duration, unit);
    }

    /**
     * 包装执行并自动计时。
     *
     * @param name     指标名称
     * @param supplier 要执行的操作
     * @param <T>      返回值类型
     * @return 操作结果
     */
    public <T> T timer(String name, Supplier<T> supplier) {
        Timer timer = Timer.builder(name).register(meterRegistry);
        return timer.record(supplier);
    }

    /**
     * 包装执行并自动计时（无返回值）。
     *
     * @param name     指标名称
     * @param runnable 要执行的操作
     */
    public void timer(String name, Runnable runnable) {
        Timer timer = Timer.builder(name).register(meterRegistry);
        timer.record(runnable);
    }
}
