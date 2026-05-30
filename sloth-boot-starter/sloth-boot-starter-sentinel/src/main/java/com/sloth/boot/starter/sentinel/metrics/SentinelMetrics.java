package com.sloth.boot.starter.sentinel.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;

/**
 * Sentinel 指标桥接。
 * <p>
 * 通过 Micrometer 暴露 Sentinel 的限流/熔断计数，支持接入 Prometheus。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class SentinelMetrics {

    private final Counter blockCount;
    private final Counter passCount;
    private final Counter exceptionCount;

    public SentinelMetrics(MeterRegistry registry) {
        this.blockCount = Counter.builder("sentinel.block.count")
            .description("被限流/熔断的请求次数").register(registry);
        this.passCount = Counter.builder("sentinel.pass.count")
            .description("通过的请求次数").register(registry);
        this.exceptionCount = Counter.builder("sentinel.exception.count")
            .description("异常请求次数").register(registry);
    }
}
