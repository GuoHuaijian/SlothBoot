package com.sloth.boot.example.observability.infrastructure.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 应用基础配置。
 * <p>
 * OTel Java Agent 在 JVM 级别自动完成 Metrics / Traces / Logs 的采集与导出，
 * 应用侧通过 {@link GlobalOpenTelemetry} 获取 Agent 注入的 Meter 实例注册自定义业务指标。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Configuration
public class AppConfig {

    @Value("${spring.application.name:sloth-observability-demo}")
    private String serviceName;

    @Bean
    public Meter otelMeter() {
        return GlobalOpenTelemetry.getMeter(serviceName);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
