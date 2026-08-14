package com.sloth.boot.starter.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 链路追踪配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sloth.monitor.tracing")
public class TracingProperties {

    /**
     * 是否启用链路追踪。
     */
    private boolean enabled = true;

    /**
     * 链路采样率，范围 0.0 ~ 1.0。
     * 1.0 表示全量采集，0.0 表示不采集。
     */
    private double samplerRate = 1.0;
}
