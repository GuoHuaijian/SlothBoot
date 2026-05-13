package com.sloth.boot.example.service;

import com.sloth.boot.example.dto.JvmInfo;
import com.sloth.boot.example.dto.MetricSummary;
import com.sloth.boot.starter.monitor.metrics.BusinessMetrics;
import com.sloth.boot.starter.thread.core.ThreadPoolManager;
import com.sloth.boot.starter.thread.core.ThreadPoolRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 监控示例服务。
 * <p>
 * 演示 JVM 指标采集、线程池监控、业务指标统计和健康检查等能力。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorDemoService {

    private final Environment environment;
    private final ThreadPoolManager threadPoolManager;
    private final ThreadPoolRegistry threadPoolRegistry;
    private final BusinessMetrics businessMetrics;
    private final MeterRegistry meterRegistry;

    /**
     * 获取应用基本信息。
     */
    public Map<String, Object> getAppInfo() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", environment.getProperty("spring.application.name"));
        info.put("jdkVersion", System.getProperty("java.version"));
        info.put("springBootVersion", SpringBootVersion.getVersion());
        info.put("startTime", formatTimestamp(runtimeMXBean.getStartTime()));
        info.put("uptime", formatDuration(runtimeMXBean.getUptime()));
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        return info;
    }

    /**
     * 获取 JVM 详细信息。
     */
    public JvmInfo getJvmInfo() {
        // 内存信息
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

        // 线程信息
        var threadMXBean = ManagementFactory.getThreadMXBean();

        // GC 信息
        List<JvmInfo.GcInfo> gcInfos = ManagementFactory.getGarbageCollectorMXBeans().stream()
                .map(gc -> JvmInfo.GcInfo.builder()
                        .name(gc.getName())
                        .collectionCount(gc.getCollectionCount())
                        .collectionTime(gc.getCollectionTime() + "ms")
                        .build())
                .collect(Collectors.toList());

        // 堆使用率
        double heapUsagePercent = heapUsage.getMax() > 0
                ? (double) heapUsage.getUsed() / heapUsage.getMax() * 100
                : 0;

        return JvmInfo.builder()
                .heapUsed(bytesToMB(heapUsage.getUsed()))
                .heapMax(bytesToMB(heapUsage.getMax()))
                .heapCommitted(bytesToMB(heapUsage.getCommitted()))
                .heapUsagePercent(Math.round(heapUsagePercent * 100.0) / 100.0)
                .nonHeapUsed(bytesToMB(nonHeapUsage.getUsed()))
                .nonHeapCommitted(bytesToMB(nonHeapUsage.getCommitted()))
                .threadCount(threadMXBean.getThreadCount())
                .peakThreadCount(threadMXBean.getPeakThreadCount())
                .daemonThreadCount(threadMXBean.getDaemonThreadCount())
                .gcInfos(gcInfos)
                .cpuProcessors(Runtime.getRuntime().availableProcessors())
                .systemLoadAverage(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage())
                .build();
    }

    /**
     * 获取所有线程池快照。
     */
    public Map<String, com.sloth.boot.starter.thread.core.ThreadPoolSnapshot> getThreadPoolSnapshots() {
        return threadPoolRegistry.getAllSnapshots();
    }

    /**
     * 获取指定线程池快照。
     */
    public com.sloth.boot.starter.thread.core.ThreadPoolSnapshot getThreadPoolSnapshot(String name) {
        return threadPoolManager.getSnapshot(name);
    }

    /**
     * 动态调整线程池大小。
     */
    public void resizeThreadPool(String name, int coreSize, int maxSize) {
        threadPoolManager.updatePoolSize(name, coreSize, maxSize);
    }

    /**
     * 向指定线程池提交任务。
     */
    public Map<String, Object> submitTasks(String poolName, int count, long sleepMs) {
        ExecutorService executor = threadPoolRegistry.getPool(poolName);
        if (executor == null) {
            throw new IllegalArgumentException("线程池不存在: " + poolName);
        }
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("submitted", count);
        result.put("poolName", poolName);
        return result;
    }

    /**
     * 递增业务计数器。
     */
    public void incrementCounter(String name) {
        businessMetrics.increment(name);
    }

    /**
     * 记录业务耗时。
     */
    public void recordTimer(String name, long durationMs) {
        businessMetrics.timer(name, () -> {
            try {
                Thread.sleep(durationMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * 获取指标汇总信息。
     */
    public MetricSummary getMetricsSummary() {
        List<MetricSummary.CounterInfo> counters = new ArrayList<>();
        List<MetricSummary.TimerInfo> timers = new ArrayList<>();

        meterRegistry.getMeters().forEach(meter -> {
            if (meter instanceof Counter counter) {
                Map<String, String> tags = counter.getId().getTags().stream()
                        .collect(Collectors.toMap(
                            Tag::getKey,
                            Tag::getValue,
                                (a, b) -> b));
                counters.add(MetricSummary.CounterInfo.builder()
                        .name(counter.getId().getName())
                        .tags(tags)
                        .count(counter.count())
                        .build());
            } else if (meter instanceof Timer timer) {
                Map<String, String> tags = timer.getId().getTags().stream()
                        .collect(Collectors.toMap(
                            Tag::getKey,
                            Tag::getValue,
                                (a, b) -> b));
                timers.add(MetricSummary.TimerInfo.builder()
                        .name(timer.getId().getName())
                        .tags(tags)
                        .count(timer.count())
                        .totalTime(String.format("%.2fms", timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS)))
                        .mean(String.format("%.2fms", timer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)))
                        .max(String.format("%.2fms", timer.max(java.util.concurrent.TimeUnit.MILLISECONDS)))
                        .build());
            }
        });

        return MetricSummary.builder()
                .counters(counters)
                .timers(timers)
                .build();
    }

    /**
     * 获取健康详情。
     */
    public Map<String, Object> getHealthDetail() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("status", "UP");
        detail.put("threadPools", threadPoolRegistry.getAllSnapshots());
        detail.put("timestamp", System.currentTimeMillis());
        return detail;
    }

    /**
     * 模拟慢接口，触发慢接口告警。
     */
    public String slowApi() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "慢接口执行完成，已触发慢接口告警（阈值：3000ms）";
    }

    private String bytesToMB(long bytes) {
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private String formatTimestamp(long millis) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(millis));
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        if (days > 0) {
            return String.format("%d天%d小时%d分%d秒", days, hours, minutes, secs);
        } else if (hours > 0) {
            return String.format("%d小时%d分%d秒", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%d分%d秒", minutes, secs);
        } else {
            return String.format("%d秒", secs);
        }
    }
}
