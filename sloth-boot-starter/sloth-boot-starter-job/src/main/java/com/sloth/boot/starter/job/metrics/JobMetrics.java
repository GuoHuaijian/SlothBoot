package com.sloth.boot.starter.job.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;

/**
 * Job 执行指标。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class JobMetrics {

    private final Counter executionTotal;
    private final Counter executionSuccess;
    private final Counter executionFailure;
    private final Timer executionDuration;

    public JobMetrics(MeterRegistry registry) {
        this.executionTotal = Counter.builder("job.execution.total")
            .description("Job 执行总次数").register(registry);
        this.executionSuccess = Counter.builder("job.execution.success")
            .description("Job 执行成功次数").register(registry);
        this.executionFailure = Counter.builder("job.execution.failure")
            .description("Job 执行失败次数").register(registry);
        this.executionDuration = Timer.builder("job.execution.duration")
            .description("Job 执行耗时").register(registry);
    }
}
