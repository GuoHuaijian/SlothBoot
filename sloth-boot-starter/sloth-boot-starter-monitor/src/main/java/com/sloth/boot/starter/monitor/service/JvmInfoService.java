package com.sloth.boot.starter.monitor.service;

import com.sloth.boot.starter.monitor.model.JvmInfo;
import com.sloth.boot.starter.monitor.util.MonitorUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JVM 信息采集服务。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class JvmInfoService implements JvmInfoProvider {

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
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

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
                .heapUsed(MonitorUtil.bytesToMB(heapUsage.getUsed()))
                .heapMax(MonitorUtil.bytesToMB(heapUsage.getMax()))
                .heapCommitted(MonitorUtil.bytesToMB(heapUsage.getCommitted()))
                .heapUsagePercent(Math.round(heapUsagePercent * 100.0) / 100.0)
                .nonHeapUsed(MonitorUtil.bytesToMB(nonHeapUsage.getUsed()))
                .nonHeapCommitted(MonitorUtil.bytesToMB(nonHeapUsage.getCommitted()))
                .threadCount(threadMXBean.getThreadCount())
                .peakThreadCount(threadMXBean.getPeakThreadCount())
                .daemonThreadCount(threadMXBean.getDaemonThreadCount())
                .gcInfos(gcInfos)
                .cpuProcessors(Runtime.getRuntime().availableProcessors())
                .systemLoadAverage(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage())
                .build();
    }
}
