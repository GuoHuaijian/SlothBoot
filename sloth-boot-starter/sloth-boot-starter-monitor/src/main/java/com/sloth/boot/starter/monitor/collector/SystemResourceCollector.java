package com.sloth.boot.starter.monitor.collector;

import com.sloth.boot.starter.monitor.alarm.AlarmService;
import com.sloth.boot.starter.monitor.config.MonitorProperties;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统资源采集器。
 * <p>
 * 定期采集 CPU、JVM 内存和磁盘使用率，通过 Micrometer Gauge 暴露指标，
 * 并在使用率超过阈值时触发告警。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class SystemResourceCollector {

    private final MonitorProperties monitorProperties;
    private final ObjectProvider<AlarmService> alarmServiceProvider;
    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;

    /**
     * 构造函数，注册 Micrometer Gauge 指标。
     *
     * @param monitorProperties 监控配置
     * @param alarmServiceProvider 告警服务提供者
     * @param meterRegistry 指标注册中心
     */
    public SystemResourceCollector(MonitorProperties monitorProperties,
                                   ObjectProvider<AlarmService> alarmServiceProvider,
                                   MeterRegistry meterRegistry) {
        this.monitorProperties = monitorProperties;
        this.alarmServiceProvider = alarmServiceProvider;
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.memoryBean = ManagementFactory.getMemoryMXBean();

        meterRegistry.gauge("system.cpu.usage", this, collector -> collector.getCpuUsage());
        meterRegistry.gauge("system.memory.usage", this, collector -> collector.getMemoryUsage());
        meterRegistry.gauge("system.disk.usage", this, collector -> collector.getDiskUsage());
    }

    /**
     * 获取 CPU 使用率（百分比，0-100）。
     *
     * @return CPU 使用率百分比
     */
    public double getCpuUsage() {
        double loadAverage = osBean.getSystemLoadAverage();
        int processors = osBean.getAvailableProcessors();
        if (loadAverage < 0 || processors <= 0) {
            return 0.0;
        }
        return (loadAverage / processors) * 100.0;
    }

    /**
     * 获取 JVM 堆内存使用率（百分比，0-100）。
     *
     * @return 内存使用率百分比
     */
    public double getMemoryUsage() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long used = heapUsage.getUsed();
        long max = heapUsage.getMax();
        if (max <= 0) {
            return 0.0;
        }
        return ((double) used / max) * 100.0;
    }

    /**
     * 获取磁盘使用率（百分比，0-100）。
     *
     * @return 磁盘使用率百分比
     */
    public double getDiskUsage() {
        File[] roots = File.listRoots();
        if (roots == null || roots.length == 0) {
            return 0.0;
        }
        long totalSpace = 0;
        long usableSpace = 0;
        for (File root : roots) {
            totalSpace += root.getTotalSpace();
            usableSpace += root.getUsableSpace();
        }
        if (totalSpace <= 0) {
            return 0.0;
        }
        return (1.0 - (double) usableSpace / totalSpace) * 100.0;
    }

    /**
     * 定期采集系统资源并在超过阈值时发送告警。
     */
    @Scheduled(fixedDelayString = "${sloth.monitor.collect-interval:60000}")
    public void collectAndAlarm() {
        if (!monitorProperties.getAlarm().isEnabled()) {
            return;
        }

        AlarmService alarmService = alarmServiceProvider.getIfAvailable();
        if (alarmService == null) {
            return;
        }

        MonitorProperties.Alarm alarm = monitorProperties.getAlarm();
        double cpuUsage = getCpuUsage();
        double memoryUsage = getMemoryUsage();
        double diskUsage = getDiskUsage();

        List<String> exceeded = new ArrayList<>();
        if (cpuUsage > alarm.getCpuThreshold()) {
            exceeded.add(String.format("CPU 使用率: %.1f%% (阈值: %.1f%%)", cpuUsage, alarm.getCpuThreshold()));
        }
        if (memoryUsage > alarm.getMemoryThreshold()) {
            exceeded.add(String.format("内存使用率: %.1f%% (阈值: %.1f%%)", memoryUsage, alarm.getMemoryThreshold()));
        }
        if (diskUsage > alarm.getDiskThreshold()) {
            exceeded.add(String.format("磁盘使用率: %.1f%% (阈值: %.1f%%)", diskUsage, alarm.getDiskThreshold()));
        }

        if (!exceeded.isEmpty()) {
            String content = String.join("\n", exceeded);
            log.warn("系统资源告警: {}", content);
            try {
                alarmService.send("系统资源告警", content);
            } catch (Exception e) {
                log.error("发送系统资源告警失败", e);
            }
        }
    }
}
