package com.sloth.boot.example.controller.monitor;

import com.sloth.boot.common.result.R;
import com.sloth.boot.starter.monitor.endpoint.InfoEndpoint;
import com.sloth.boot.starter.monitor.model.JvmInfo;
import com.sloth.boot.starter.monitor.model.MetricSummary;
import com.sloth.boot.example.service.monitor.MonitorDemoService;
import com.sloth.boot.starter.thread.core.ThreadPoolSnapshot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统监控演示接口
 * <p>
 * 演示 JVM 监控、线程池管理、Micrometer 业务指标、慢接口检测等能力
 */
@Tag(name = "系统监控", description = "演示 JVM 监控、线程池监控、Micrometer 指标、慢接口检测")
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorDemoService monitorService;
    private final InfoEndpoint infoEndpoint;

    @Operation(summary = "应用信息", description = "获取应用名称、版本、构建信息、部署环境、JDK、主机等详细信息")
    @GetMapping("/app-info")
    public R<Map<String, Object>> appInfo() {
        return R.ok(infoEndpoint.info());
    }

    @Operation(summary = "JVM 信息", description = "获取 JVM 内存、线程、GC 等详细指标")
    @GetMapping("/jvm")
    public R<JvmInfo> jvm() {
        return R.ok(monitorService.getJvmInfo());
    }

    @Operation(summary = "线程池列表", description = "获取所有注册线程池的快照信息")
    @GetMapping("/thread-pools")
    public R<Map<String, ThreadPoolSnapshot>> threadPools() {
        return R.ok(monitorService.getThreadPoolSnapshots());
    }

    @Operation(summary = "线程池详情", description = "获取指定线程池的详细快照")
    @Parameter(name = "name", description = "线程池名称", required = true, example = "default")
    @GetMapping("/thread-pool/{name}")
    public R<Object> threadPool(@PathVariable String name) {
        var snapshot = monitorService.getThreadPoolSnapshot(name);
        if (snapshot == null) {
            return R.fail("线程池不存在: " + name);
        }
        return R.ok(snapshot);
    }

    @Operation(summary = "调整线程池", description = "动态调整指定线程池的核心线程数和最大线程数")
    @Parameter(name = "name", description = "线程池名称", required = true, example = "default")
    @Parameter(name = "coreSize", description = "核心线程数", required = true, example = "5")
    @Parameter(name = "maxSize", description = "最大线程数", required = true, example = "20")
    @PostMapping("/thread-pool/resize")
    public R<String> resizeThreadPool(@RequestParam String name,
                                      @RequestParam int coreSize,
                                      @RequestParam int maxSize) {
        monitorService.resizeThreadPool(name, coreSize, maxSize);
        return R.ok("线程池 " + name + " 已调整: core=" + coreSize + ", max=" + maxSize);
    }

    @Operation(summary = "提交任务", description = "向指定线程池提交模拟任务，用于观察线程池行为")
    @Parameter(name = "poolName", description = "线程池名称", example = "default")
    @Parameter(name = "count", description = "任务数量", example = "10")
    @Parameter(name = "sleepMs", description = "每个任务的休眠毫秒数", example = "100")
    @PostMapping("/thread-pool/submit")
    public R<Map<String, Object>> submitTasks(@RequestParam(defaultValue = "default") String poolName,
                                              @RequestParam(defaultValue = "10") int count,
                                              @RequestParam(defaultValue = "100") long sleepMs) {
        return R.ok(monitorService.submitTasks(poolName, count, sleepMs));
    }

    @Operation(summary = "递增计数器", description = "递增指定名称的 Micrometer 计数器")
    @Parameter(name = "name", description = "计数器名称", example = "demo.request")
    @PostMapping("/metrics/counter")
    public R<String> incrementCounter(@RequestParam(defaultValue = "demo.request") String name) {
        monitorService.incrementCounter(name);
        return R.ok("计数器 " + name + " 已递增");
    }

    @Operation(summary = "记录耗时", description = "记录一次指定名称的操作耗时到 Micrometer Timer")
    @Parameter(name = "name", description = "计时器名称", example = "demo.operation")
    @Parameter(name = "durationMs", description = "模拟耗时毫秒数", example = "100")
    @PostMapping("/metrics/timer")
    public R<String> recordTimer(@RequestParam(defaultValue = "demo.operation") String name,
                                 @RequestParam(defaultValue = "100") long durationMs) {
        monitorService.recordTimer(name, durationMs);
        return R.ok("耗时记录完成");
    }

    @Operation(summary = "指标汇总", description = "获取所有 Micrometer 计数器和计时器的汇总信息")
    @GetMapping("/metrics/summary")
    public R<MetricSummary> metricsSummary() {
        return R.ok(monitorService.getMetricsSummary());
    }

    @Operation(summary = "慢接口检测", description = "模拟一个耗时超过 3 秒的慢接口，触发慢接口告警")
    @GetMapping("/slow-api")
    public R<String> slowApi() {
        return R.ok(monitorService.slowApi());
    }
}
