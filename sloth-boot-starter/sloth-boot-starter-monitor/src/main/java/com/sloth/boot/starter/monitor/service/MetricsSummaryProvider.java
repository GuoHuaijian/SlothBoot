package com.sloth.boot.starter.monitor.service;

import com.sloth.boot.starter.monitor.model.MetricSummary;

/**
 * 指标汇总能力接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface MetricsSummaryProvider {

    /**
     * 获取指标汇总信息。
     *
     * @return 指标汇总信息
     */
    MetricSummary getMetricsSummary();
}
