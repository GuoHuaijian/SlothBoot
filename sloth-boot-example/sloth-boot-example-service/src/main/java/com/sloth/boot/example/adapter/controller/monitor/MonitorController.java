package com.sloth.boot.example.adapter.controller.monitor;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.monitor.MonitorCommand;
import com.sloth.boot.starter.monitor.endpoint.InfoEndpoint;
import com.sloth.boot.starter.monitor.endpoint.SystemResourceEndpoint;
import com.sloth.boot.starter.monitor.model.JvmInfo;
import com.sloth.boot.starter.monitor.model.MetricSummary;
import com.sloth.boot.starter.threadpool.core.ThreadPoolSnapshot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统监控接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "系统监控", description = "演示 JVM 监控、线程池监控、Micrometer 指标、慢接口检测")
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorCommand monitorCommand;
    private final InfoEndpoint infoEndpoint;
    private final SystemResourceEndpoint systemResourceEndpoint;

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        return R.ok(monitorCommand.getHealthStatus());
    }

    @Operation(summary = "应用信息")
    @GetMapping("/app-info")
    public R<Map<String, Object>> appInfo() {
        return R.ok(infoEndpoint.info());
    }

    @Operation(summary = "JVM 信息")
    @GetMapping("/jvm")
    public R<JvmInfo> jvm() {
        return R.ok(monitorCommand.getJvmInfo());
    }

    @Operation(summary = "线程池列表")
    @GetMapping("/thread-pools")
    public R<Map<String, ThreadPoolSnapshot>> threadPools() {
        return R.ok(monitorCommand.getThreadPoolSnapshots());
    }

    @Operation(summary = "线程池详情")
    @Parameter(name = "name", description = "线程池名称", required = true, example = "default")
    @GetMapping("/thread-pool/{name}")
    public R<Object> threadPool(@PathVariable String name) {
        var snapshot = monitorCommand.getThreadPoolSnapshot(name);
        if (snapshot == null) {
            return R.fail("线程池不存在: " + name);
        }
        return R.ok(snapshot);
    }

    @Operation(summary = "调整线程池")
    @PostMapping("/thread-pool/resize")
    public R<String> resizeThreadPool(@RequestParam String name, @RequestParam int coreSize, @RequestParam int maxSize) {
        monitorCommand.resizeThreadPool(name, coreSize, maxSize);
        return R.ok("线程池 " + name + " 已调整: core=" + coreSize + ", max=" + maxSize);
    }

    @Operation(summary = "提交任务")
    @PostMapping("/thread-pool/submit")
    public R<Map<String, Object>> submitTasks(@RequestParam(defaultValue = "default") String poolName,
                                              @RequestParam(defaultValue = "10") int count,
                                              @RequestParam(defaultValue = "100") long sleepMs) {
        return R.ok(monitorCommand.submitTasks(poolName, count, sleepMs));
    }

    @Operation(summary = "递增计数器")
    @PostMapping("/metrics/counter")
    public R<String> incrementCounter(@RequestParam(defaultValue = "demo.request") String name) {
        monitorCommand.incrementCounter(name);
        return R.ok("计数器 " + name + " 已递增");
    }

    @Operation(summary = "记录耗时")
    @PostMapping("/metrics/timer")
    public R<String> recordTimer(@RequestParam(defaultValue = "demo.operation") String name,
                                 @RequestParam(defaultValue = "100") long durationMs) {
        monitorCommand.recordTimer(name, durationMs);
        return R.ok("耗时记录完成");
    }

    @Operation(summary = "指标汇总")
    @GetMapping("/metrics/summary")
    public R<MetricSummary> metricsSummary() {
        return R.ok(monitorCommand.getMetricsSummary());
    }

    @Operation(summary = "慢接口检测")
    @GetMapping("/slow-api")
    public R<String> slowApi() {
        return R.ok(monitorCommand.slowApi());
    }
}
