package com.sloth.boot.example.observability.application.command;

import com.sloth.boot.example.observability.application.model.vo.MetricsDemoVO;
import com.sloth.boot.example.observability.application.model.vo.SlowOperationVO;
import com.sloth.boot.example.observability.application.model.vo.TraceDemoVO;
import com.sloth.boot.starter.threadpool.core.ThreadPools;
import com.sloth.boot.starter.threadpool.core.VisibleThreadPoolExecutor;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 可观测性演示业务命令。
 * <p>
 * 承载慢操作、模拟异常、链路追踪、自定义指标等演示逻辑，并通过 OTel Agent 注入的
 * {@link Meter} 注册自定义业务指标。
 * 使用预置的 {@link ThreadPools#DEFAULT} 线程池。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
public class DemoCommand {

    private final VisibleThreadPoolExecutor executor;
    private final LongCounter errorCounter;
    private final LongCounter customCounter;
    private final DoubleHistogram processingTimer;

    public DemoCommand(Meter meter) {
        this.executor = ThreadPools.defaultPool();

        this.errorCounter = meter.counterBuilder("demo.errors")
                .setDescription("Business errors")
                .build();
        this.customCounter = meter.counterBuilder("demo.custom.metric")
                .setDescription("Custom business metric incremented on each call")
                .build();
        this.processingTimer = meter.histogramBuilder("demo.timer.processing")
                .setDescription("Custom processing timer")
                .setUnit("ms")
                .build();
    }

    /**
     * 模拟慢操作。
     * <p>
     * 睡眠 2500–3500ms，用于触发慢请求告警与延迟分布观测。
     *
     * @return 慢操作结果
     */
    public SlowOperationVO slow() {
        log.info("Slow endpoint called - simulating heavy processing");
        int delay = ThreadLocalRandom.current().nextInt(2500, 3500);
        sleep(delay);
        log.warn("Slow endpoint completed after {}ms", delay);
        return new SlowOperationVO(delay, "This endpoint simulates a slow operation");
    }

    /**
     * 模拟业务异常并递增错误计数器。
     *
     * @throws RuntimeException 始终抛出，用于演示异常链路与错误指标
     */
    public void error() {
        log.error("Simulating business exception");
        errorCounter.add(1);
        throw new RuntimeException("Simulated business exception for observability demo");
    }

    /**
     * 链路追踪演示。
     * <p>
     * 读取 MDC 中的 traceId/spanId，并向异步线程池提交子任务以演示 trace 上下文透传。
     *
     * @return 链路追踪演示结果
     */
    public TraceDemoVO trace() {
        log.info("Trace demo - parent span");
        String traceId = org.slf4j.MDC.get("traceId");
        String spanId = org.slf4j.MDC.get("spanId");

        final String capturedTraceId = traceId;
        executor.submit(() -> {
            log.info("Trace demo - async child span (traceId={})", capturedTraceId);
            sleep(ThreadLocalRandom.current().nextInt(50, 150));
        });

        int delay = ThreadLocalRandom.current().nextInt(30, 100);
        sleep(delay);
        return new TraceDemoVO(
                traceId != null ? traceId : "N/A (no OTel Agent)",
                spanId != null ? spanId : "N/A",
                delay
        );
    }

    /**
     * 自定义指标演示。
     * <p>
     * 递增 {@code demo.custom.metric} 计数器，并向 {@code demo.timer.processing} 直方图记录随机值。
     *
     * @return 自定义指标演示结果
     */
    public MetricsDemoVO metrics() {
        customCounter.add(1);
        processingTimer.record(ThreadLocalRandom.current().nextInt(10, 60));
        return new MetricsDemoVO("demo.custom.metric", "demo.timer.processing");
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
