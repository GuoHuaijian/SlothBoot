package com.sloth.boot.example.observability;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 可观测性演示应用。
 * <p>
 * 集成 OpenTelemetry + Tempo + Prometheus + Loki + Grafana 全栈可观测性方案。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.sloth.boot.example.observability.infrastructure.repository.mapper")
public class ObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityApplication.class, args);
    }
}
