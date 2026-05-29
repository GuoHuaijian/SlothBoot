package com.sloth.boot.example.service.monitor;

import com.sloth.boot.starter.monitor.model.JvmInfo;
import com.sloth.boot.starter.monitor.model.MetricSummary;
import com.sloth.boot.starter.monitor.service.JvmInfoService;
import com.sloth.boot.starter.monitor.service.MetricsSummaryService;
import com.sloth.boot.starter.monitor.metrics.BusinessMetrics;
import com.sloth.boot.starter.thread.core.ThreadPoolManager;
import com.sloth.boot.starter.thread.core.ThreadPoolRegistry;
import com.sloth.boot.starter.thread.core.ThreadPoolSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 监控示例服务。
 * <p>
 * 薄代理层，将监控逻辑委托给 starter 提供的服务。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorDemoService {

    private final JvmInfoService jvmInfoService;
    private final MetricsSummaryService metricsSummaryService;
    private final ThreadPoolManager threadPoolManager;
    private final ThreadPoolRegistry threadPoolRegistry;
    private final BusinessMetrics businessMetrics;

    /**
     * 获取 JVM 详细信息。
     *
     * @return JVM 详细信息
     */
    public JvmInfo getJvmInfo() {
        return jvmInfoService.getJvmInfo();
    }

    /**
     * 获取指标汇总信息。
     *
     * @return 指标汇总信息
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
     * @return 线程池快照
     */
    public ThreadPoolSnapshot getThreadPoolSnapshot(String name) {
        return threadPoolManager.getSnapshot(name);
    }

    /**
     * 动态调整线程池大小。
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
     * @param count    任务数量
     * @param sleepMs  每个任务休眠时间（毫秒）
     * @return 提交结果
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
     *
     * @param name 计数器名称
     */
    public void incrementCounter(String name) {
        businessMetrics.increment(name);
    }

    /**
     * 记录业务耗时。
     *
     * @param name       计时器名称
     * @param durationMs 耗时（毫秒）
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
     * 模拟慢接口，触发慢接口告警。
     *
     * @return 执行结果提示
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
