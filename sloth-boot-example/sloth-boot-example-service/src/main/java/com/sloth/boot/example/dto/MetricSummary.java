package com.sloth.boot.example.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MetricSummary {

    private List<CounterInfo> counters;

    private List<TimerInfo> timers;

    @Data
    @Builder
    public static class CounterInfo {

        private String name;

        private Map<String, String> tags;

        private double count;
    }

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
