package com.sloth.boot.starter.monitor.service;

import com.sloth.boot.starter.monitor.model.JvmInfo;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JVM 信息采集服务。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Service
public class JvmInfoService {

    /**
     * 获取 JVM 详细信息。
     *
     * @return JVM 详细信息
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

    static String bytesToMB(long bytes) {
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
