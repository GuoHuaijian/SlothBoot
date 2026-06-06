package com.sloth.boot.starter.monitor.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 指标汇总信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
public class MetricSummary {

    /** 计数器列表 */
    private List<CounterInfo> counters;

    /** 计时器列表 */
    private List<TimerInfo> timers;

    /**
     * 计数器信息。
     */
    @Data
    @Builder
    public static class CounterInfo {

        /** 指标名称 */
        private String name;

        /** 标签键值对 */
        private Map<String, String> tags;

        /** 当前计数值 */
        private double count;
    }

    /**
     * 计时器信息。
     */
    @Data
    @Builder
    public static class TimerInfo {

        /** 指标名称 */
        private String name;

        /** 标签键值对 */
        private Map<String, String> tags;

        /** 采样次数 */
        private long count;

        /** 总耗时 */
        private String totalTime;

        /** 平均耗时 */
        private String mean;

        /** 最大耗时 */
        private String max;
    }
}
