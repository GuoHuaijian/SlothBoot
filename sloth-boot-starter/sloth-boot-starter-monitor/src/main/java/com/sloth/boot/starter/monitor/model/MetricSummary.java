package com.sloth.boot.starter.monitor.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "指标汇总信息")
public class MetricSummary {

    /** 计数器列表 */
    @Schema(description = "计数器列表")
    private List<CounterInfo> counters;

    /** 计时器列表 */
    @Schema(description = "计时器列表")
    private List<TimerInfo> timers;

    /**
     * 计数器信息。
     */
    @Data
    @Builder
    @Schema(description = "计数器信息")
    public static class CounterInfo {

        /** 指标名称 */
        @Schema(description = "指标名称", example = "demo.request")
        private String name;

        /** 标签键值对 */
        @Schema(description = "标签键值对")
        private Map<String, String> tags;

        /** 当前计数值 */
        @Schema(description = "当前计数值", example = "42.0")
        private double count;
    }

    /**
     * 计时器信息。
     */
    @Data
    @Builder
    @Schema(description = "计时器信息")
    public static class TimerInfo {

        /** 指标名称 */
        @Schema(description = "指标名称", example = "demo.operation")
        private String name;

        /** 标签键值对 */
        @Schema(description = "标签键值对")
        private Map<String, String> tags;

        /** 采样次数 */
        @Schema(description = "采样次数", example = "100")
        private long count;

        /** 总耗时 */
        @Schema(description = "总耗时", example = "5s")
        private String totalTime;

        /** 平均耗时 */
        @Schema(description = "平均耗时", example = "50ms")
        private String mean;

        /** 最大耗时 */
        @Schema(description = "最大耗时", example = "200ms")
        private String max;
    }
}
