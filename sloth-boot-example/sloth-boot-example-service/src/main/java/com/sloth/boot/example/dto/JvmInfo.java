package com.sloth.boot.example.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JvmInfo {

    private String heapUsed;

    private String heapMax;

    private String heapCommitted;

    private double heapUsagePercent;

    private String nonHeapUsed;

    private String nonHeapCommitted;

    private int threadCount;

    private int peakThreadCount;

    private int daemonThreadCount;

    private List<GcInfo> gcInfos;

    private int cpuProcessors;

    private double systemLoadAverage;

    @Data
    @Builder
    public static class GcInfo {

        private String name;

        private long collectionCount;

        private String collectionTime;
    }
}
