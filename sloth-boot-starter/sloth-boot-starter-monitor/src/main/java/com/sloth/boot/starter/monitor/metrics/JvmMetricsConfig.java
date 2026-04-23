package com.sloth.boot.starter.monitor.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;

/**
 * JVM 指标配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class JvmMetricsConfig {

    /**
     * 注册 JVM 指标。
     *
     * @param meterRegistry 指标注册中心
     */
    public JvmMetricsConfig(MeterRegistry meterRegistry) {
        new JvmMemoryMetrics().bindTo(meterRegistry);
        new JvmGcMetrics().bindTo(meterRegistry);
        new JvmThreadMetrics().bindTo(meterRegistry);
        new ClassLoaderMetrics().bindTo(meterRegistry);
    }
}
