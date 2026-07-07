package com.sloth.boot.example.observability.application.helper;

import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;

/**
 * OpenTelemetry 指标懒加载工具。
 * <p>
 * 演示侧多处需要按需构建 {@link LongCounter} / {@link DoubleHistogram}，统一在此封装
 * 双检锁懒加载，避免重复样板代码。OTel 指标构建器本身线程安全，懒加载仅用于
 * 单例化已构建的句柄。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class MetricsSupport {

    private MetricsSupport() {
    }

    /**
     * 懒加载长整型计数器。
     *
     * @param meter        OTel Meter
     * @param holder       计数器持有者
     * @param name         指标名
     * @param description  指标描述
     * @return 计数器实例
     */
    public static LongCounter lazyCounter(Meter meter, LongCounterHolder holder, String name, String description) {
        LongCounter counter = holder.get();
        if (counter == null) {
            synchronized (holder) {
                counter = holder.get();
                if (counter == null) {
                    counter = meter.counterBuilder(name).setDescription(description).build();
                    holder.set(counter);
                }
            }
        }
        return counter;
    }

    /**
     * 懒加载直方图（毫秒单位）。
     *
     * @param meter        OTel Meter
     * @param holder       直方图持有者
     * @param name         指标名
     * @param description  指标描述
     * @return 直方图实例
     */
    public static DoubleHistogram lazyHistogram(Meter meter, DoubleHistogramHolder holder,
                                                String name, String description) {
        DoubleHistogram histogram = holder.get();
        if (histogram == null) {
            synchronized (holder) {
                histogram = holder.get();
                if (histogram == null) {
                    histogram = meter.histogramBuilder(name)
                            .setDescription(description)
                            .setUnit("ms")
                            .build();
                    holder.set(histogram);
                }
            }
        }
        return histogram;
    }

    /** 可变长整型计数器持有者，承载双检锁的读写槽位。 */
    public static final class LongCounterHolder {
        private volatile LongCounter value;

        public LongCounter get() {
            return value;
        }

        public void set(LongCounter value) {
            this.value = value;
        }
    }

    /** 可变直方图持有者，承载双检锁的读写槽位。 */
    public static final class DoubleHistogramHolder {
        private volatile DoubleHistogram value;

        public DoubleHistogram get() {
            return value;
        }

        public void set(DoubleHistogram value) {
            this.value = value;
        }
    }
}
