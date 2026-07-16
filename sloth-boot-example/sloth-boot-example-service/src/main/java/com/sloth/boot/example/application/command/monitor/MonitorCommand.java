package com.sloth.boot.example.application.command.monitor;

import com.sloth.boot.starter.monitor.metrics.BusinessMetrics;
import com.sloth.boot.starter.monitor.model.JvmInfo;
import com.sloth.boot.starter.monitor.model.MetricSummary;
import com.sloth.boot.starter.monitor.service.JvmInfoService;
import com.sloth.boot.starter.monitor.service.MetricsSummaryService;
import com.sloth.boot.starter.threadpool.core.ThreadPoolManager;
import com.sloth.boot.starter.threadpool.core.ThreadPoolRegistry;
import com.sloth.boot.starter.threadpool.core.ThreadPoolSnapshot;
import com.sloth.boot.starter.threadpool.core.ThreadPools;
import com.sloth.boot.starter.threadpool.core.VisibleThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统监控命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonitorCommand {

    private final JvmInfoService jvmInfoService;
    private final MetricsSummaryService metricsSummaryService;
    private final ThreadPoolManager threadPoolManager;
    private final ThreadPoolRegistry threadPoolRegistry;
    private final BusinessMetrics businessMetrics;

    /**
     * 获取JVM信息。
     *
     * @return JVM运行时信息，包括内存、GC、线程等
     */
    public JvmInfo getJvmInfo() {
        return jvmInfoService.getJvmInfo();
    }

    /**
     * 获取指标汇总。
     *
     * @return 系统指标汇总信息
     */
    public MetricSummary getMetricsSummary() {
        return metricsSummaryService.getMetricsSummary();
    }

    /**
     * 获取所有线程池快照。
     *
     * @return 线程池名称到快照的映射
     */
    public Map<String, ThreadPoolSnapshot> getThreadPoolSnapshots() {
        return threadPoolRegistry.getAllSnapshots();
    }

    /**
     * 获取指定线程池快照。
     *
     * @param name 线程池名称
     * @return 线程池快照，不存在时返回null
     */
    public ThreadPoolSnapshot getThreadPoolSnapshot(String name) {
        return threadPoolManager.getSnapshot(name);
    }

    /**
     * 调整线程池大小。
     *
     * @param name     线程池名称
     * @param coreSize 核心线程数
     * @param maxSize  最大线程数
     */
    public void resizeThreadPool(String name, int coreSize, int maxSize) {
        threadPoolManager.updatePoolSize(name, coreSize, maxSize);
    }

    /**
     * 向指定线程池提交任务。
     *
     * @param poolName 线程池名称
     * @param count    提交任务数量
     * @param sleepMs  任务执行时长（毫秒）
     * @return 提交结果信息
     * @throws IllegalArgumentException 当线程池不存在时抛出
     */
    public Map<String, Object> submitTasks(String poolName, int count, long sleepMs) {
        VisibleThreadPoolExecutor executor = ThreadPools.getPool(poolName);
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
     * 递增计数器。
     *
     * @param name 计数器名称
     */
    public void incrementCounter(String name) {
        businessMetrics.increment(name);
    }

    /**
     * 记录耗时。
     *
     * @param name       指标名称
     * @param durationMs 模拟耗时（毫秒）
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
     * 获取健康状态。
     * <p>
     * 包含系统状态和各线程池运行状态。
     *
     * @return 健康状态信息
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("timestamp", java.time.LocalDateTime.now().toString());

        Map<String, Object> threadPools = new LinkedHashMap<>();
        Map<String, ThreadPoolSnapshot> snapshots = threadPoolRegistry.getAllSnapshots();
        for (Map.Entry<String, ThreadPoolSnapshot> entry : snapshots.entrySet()) {
            ThreadPoolSnapshot s = entry.getValue();
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("activeCount", s.activeCount());
            info.put("maximumPoolSize", s.maximumPoolSize());
            info.put("queueSize", s.queueSize());
            info.put("rejectedCount", s.rejectedCount());
            threadPools.put(entry.getKey(), info);
        }
        result.put("threadPools", threadPools);
        return result;
    }

    /**
     * 慢接口演示。
     * <p>
     * 模拟慢接口执行，用于测试慢接口检测功能。
     *
     * @return 执行结果
     */
    public String slowApi() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "慢接口执行完成，已触发慢接口告警（阈值：3000ms）";
    }
}
