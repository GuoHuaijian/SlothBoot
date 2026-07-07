package com.sloth.boot.example.observability.infrastructure.config;

import com.sloth.boot.common.event.EventPublisher;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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

    /**
     * 操作日志事件发布器。
     * <p>
     * 供 {@code sloth-boot-starter-web} 的 OperateLogAspect 切面依赖；
     * 项目级无自动装配声明该 Bean，故在此显式提供。
     *
     * @param applicationEventPublisher Spring 事件发布器
     * @return Sloth Boot 事件发布器
     */
    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new EventPublisher(applicationEventPublisher);
    }
}
