package com.sloth.boot.starter.monitor.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * JVM 详细信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
public class JvmInfo {

    /** 堆已使用内存 */
    private String heapUsed;

    /** 堆最大内存 */
    private String heapMax;

    /** 堆已提交内存 */
    private String heapCommitted;

    /** 堆使用率（百分比） */
    private double heapUsagePercent;

    /** 非堆已使用内存 */
    private String nonHeapUsed;

    /** 非堆已提交内存 */
    private String nonHeapCommitted;

    /** 当前线程数 */
    private int threadCount;

    /** 峰值线程数 */
    private int peakThreadCount;

    /** 守护线程数 */
    private int daemonThreadCount;

    /** GC 信息列表 */
    private List<GcInfo> gcInfos;

    /** 可用处理器数 */
    private int cpuProcessors;

    /** 系统平均负载 */
    private double systemLoadAverage;

    /**
     * GC 信息。
     */
    @Data
    @Builder
    public static class GcInfo {

        /** GC 名称 */
        private String name;

        /** GC 回收次数 */
        private long collectionCount;

        /** GC 回收耗时 */
        private String collectionTime;
    }
}
