package com.sloth.boot.starter.idempotent.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;

/**
 * 幂等指标采集。
 * <p>
 * 通过 Micrometer 记录幂等操作的计数器，支持接入 Prometheus 等监控系统。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class IdempotentMetrics {

    private final Counter lockAcquired;
    private final Counter lockRejected;
    private final Counter lockTimeout;
    private final Counter tokenCreated;
    private final Counter tokenConsumed;
    private final Counter tokenRejected;

    public IdempotentMetrics(MeterRegistry meterRegistry) {
        this.lockAcquired = Counter.builder("idempotent.lock.acquired")
            .description("幂等锁获取成功次数").register(meterRegistry);
        this.lockRejected = Counter.builder("idempotent.lock.rejected")
            .description("幂等锁获取失败次数（重复请求）").register(meterRegistry);
        this.lockTimeout = Counter.builder("idempotent.lock.timeout")
            .description("幂等锁等待超时次数").register(meterRegistry);
        this.tokenCreated = Counter.builder("idempotent.token.created")
            .description("幂等 Token 创建次数").register(meterRegistry);
        this.tokenConsumed = Counter.builder("idempotent.token.consumed")
            .description("幂等 Token 消费成功次数").register(meterRegistry);
        this.tokenRejected = Counter.builder("idempotent.token.rejected")
            .description("幂等 Token 校验失败次数").register(meterRegistry);
    }
}
