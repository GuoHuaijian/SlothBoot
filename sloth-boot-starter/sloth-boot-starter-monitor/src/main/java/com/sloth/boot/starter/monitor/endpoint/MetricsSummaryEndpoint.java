package com.sloth.boot.starter.monitor.endpoint;

import com.sloth.boot.starter.monitor.model.MetricSummary;
import com.sloth.boot.starter.monitor.service.MetricsSummaryService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

/**
 * 指标汇总端点，暴露 {@code /actuator/metricsSummary}。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Endpoint(id = "metricsSummary")
public class MetricsSummaryEndpoint {

    private final MetricsSummaryService metricsSummaryService;

    /**
     * 构造函数。
     *
     * @param metricsSummaryService 指标汇总服务
     */
    public MetricsSummaryEndpoint(MetricsSummaryService metricsSummaryService) {
        this.metricsSummaryService = metricsSummaryService;
    }

    /**
     * 读取指标汇总。
     *
     * @return 指标汇总
     */
    @ReadOperation
    public MetricSummary summary() {
        return metricsSummaryService.getMetricsSummary();
    }
}
