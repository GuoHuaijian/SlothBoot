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

    private List<CounterInfo> counters;

    private List<TimerInfo> timers;

    /**
     * 计数器信息。
     */
    @Data
    @Builder
    public static class CounterInfo {

        private String name;

        private Map<String, String> tags;

        private double count;
    }

    /**
     * 计时器信息。
     */
    @Data
    @Builder
    public static class TimerInfo {

        private String name;

        private Map<String, String> tags;

        private long count;

        private String totalTime;

        private String mean;

        private String max;
    }
}
