package com.sloth.boot.starter.monitor.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "JVM 详细信息")
public class JvmInfo {

    /** 堆已使用内存 */
    @Schema(description = "堆已使用内存", example = "256MB")
    private String heapUsed;

    /** 堆最大内存 */
    @Schema(description = "堆最大内存", example = "512MB")
    private String heapMax;

    /** 堆已提交内存 */
    @Schema(description = "堆已提交内存", example = "512MB")
    private String heapCommitted;

    /** 堆使用率（百分比） */
    @Schema(description = "堆使用率（百分比）", example = "50.0")
    private double heapUsagePercent;

    /** 非堆已使用内存 */
    @Schema(description = "非堆已使用内存", example = "64MB")
    private String nonHeapUsed;

    /** 非堆已提交内存 */
    @Schema(description = "非堆已提交内存", example = "128MB")
    private String nonHeapCommitted;

    /** 当前线程数 */
    @Schema(description = "当前线程数", example = "42")
    private int threadCount;

    /** 峰值线程数 */
    @Schema(description = "峰值线程数", example = "50")
    private int peakThreadCount;

    /** 守护线程数 */
    @Schema(description = "守护线程数", example = "30")
    private int daemonThreadCount;

    /** GC 信息列表 */
    @Schema(description = "GC 信息列表")
    private List<GcInfo> gcInfos;

    /** 可用处理器数 */
    @Schema(description = "可用处理器数", example = "8")
    private int cpuProcessors;

    /** 系统平均负载 */
    @Schema(description = "系统平均负载", example = "2.5")
    private double systemLoadAverage;

    /**
     * GC 信息。
     */
    @Data
    @Builder
    @Schema(description = "GC 信息")
    public static class GcInfo {

        /** GC 名称 */
        @Schema(description = "GC 名称", example = "G1 Young Generation")
        private String name;

        /** GC 回收次数 */
        @Schema(description = "GC 回收次数", example = "100")
        private long collectionCount;

        /** GC 回收耗时 */
        @Schema(description = "GC 回收耗时", example = "500ms")
        private String collectionTime;
    }
}
