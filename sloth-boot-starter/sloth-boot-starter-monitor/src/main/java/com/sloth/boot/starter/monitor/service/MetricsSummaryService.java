package com.sloth.boot.starter.monitor.service;

import com.sloth.boot.starter.monitor.model.MetricSummary;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 指标汇总服务。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class MetricsSummaryService implements MetricsSummaryProvider {

    private final MeterRegistry meterRegistry;

    /**
     * 获取指标汇总信息。
     *
     * @return 指标汇总信息
     */
    public MetricSummary getMetricsSummary() {
        List<MetricSummary.CounterInfo> counters = new ArrayList<>();
        List<MetricSummary.TimerInfo> timers = new ArrayList<>();

        meterRegistry.getMeters().forEach(meter -> {
            if (meter instanceof Counter counter) {
                Map<String, String> tags = counter.getId().getTags().stream()
                        .collect(Collectors.toMap(Tag::getKey, Tag::getValue, (a, b) -> b));
                counters.add(MetricSummary.CounterInfo.builder()
                        .name(counter.getId().getName())
                        .tags(tags)
                        .count(counter.count())
                        .build());
            } else if (meter instanceof Timer timer) {
                Map<String, String> tags = timer.getId().getTags().stream()
                        .collect(Collectors.toMap(Tag::getKey, Tag::getValue, (a, b) -> b));
                timers.add(MetricSummary.TimerInfo.builder()
                        .name(timer.getId().getName())
                        .tags(tags)
                        .count(timer.count())
                        .totalTime(String.format("%.2fms", timer.totalTime(TimeUnit.MILLISECONDS)))
                        .mean(String.format("%.2fms", timer.mean(TimeUnit.MILLISECONDS)))
                        .max(String.format("%.2fms", timer.max(TimeUnit.MILLISECONDS)))
                        .build());
            }
        });

        return MetricSummary.builder()
                .counters(counters)
                .timers(timers)
                .build();
    }
}
