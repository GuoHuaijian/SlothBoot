package com.sloth.boot.example.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.dto.JvmInfo;
import com.sloth.boot.example.dto.MetricSummary;
import com.sloth.boot.example.service.MonitorDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorDemoService monitorService;

    @GetMapping("/app-info")
    public R<Map<String, Object>> appInfo() {
        return R.ok(monitorService.getAppInfo());
    }

    @GetMapping("/jvm")
    public R<JvmInfo> jvm() {
        return R.ok(monitorService.getJvmInfo());
    }

    @GetMapping("/thread-pools")
    public R<?> threadPools() {
        return R.ok(monitorService.getThreadPoolSnapshots());
    }

    @GetMapping("/thread-pool/{name}")
    public R<?> threadPool(@PathVariable String name) {
        var snapshot = monitorService.getThreadPoolSnapshot(name);
        if (snapshot == null) {
            return R.fail("线程池不存在: " + name);
        }
        return R.ok(snapshot);
    }

    @PostMapping("/thread-pool/resize")
    public R<String> resizeThreadPool(@RequestParam String name,
                                      @RequestParam int coreSize,
                                      @RequestParam int maxSize) {
        monitorService.resizeThreadPool(name, coreSize, maxSize);
        return R.ok("线程池 " + name + " 已调整: core=" + coreSize + ", max=" + maxSize);
    }

    @PostMapping("/thread-pool/submit")
    public R<Map<String, Object>> submitTasks(@RequestParam(defaultValue = "default") String poolName,
                                              @RequestParam(defaultValue = "10") int count,
                                              @RequestParam(defaultValue = "100") long sleepMs) {
        return R.ok(monitorService.submitTasks(poolName, count, sleepMs));
    }

    @PostMapping("/metrics/counter")
    public R<String> incrementCounter(@RequestParam(defaultValue = "demo.request") String name) {
        monitorService.incrementCounter(name);
        return R.ok("计数器 " + name + " 已递增");
    }

    @PostMapping("/metrics/timer")
    public R<String> recordTimer(@RequestParam(defaultValue = "demo.operation") String name,
                                 @RequestParam(defaultValue = "100") long durationMs) {
        monitorService.recordTimer(name, durationMs);
        return R.ok("耗时记录完成");
    }

    @GetMapping("/metrics/summary")
    public R<MetricSummary> metricsSummary() {
        return R.ok(monitorService.getMetricsSummary());
    }

    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        return R.ok(monitorService.getHealthDetail());
    }

    @GetMapping("/slow-api")
    public R<String> slowApi() {
        return R.ok(monitorService.slowApi());
    }
}
