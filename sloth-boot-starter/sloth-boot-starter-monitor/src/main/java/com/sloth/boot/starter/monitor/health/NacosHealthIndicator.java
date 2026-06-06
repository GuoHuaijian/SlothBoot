package com.sloth.boot.starter.monitor.health;

import com.sloth.boot.starter.monitor.config.MonitorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.core.env.Environment;

import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Nacos 健康检查器。
 * <p>
 * 通过 HTTP 请求 Nacos 的 readiness 端点验证连通性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class NacosHealthIndicator extends AbstractHealthIndicator {

    private final Environment environment;
    private final MonitorProperties.Health healthConfig;

    public NacosHealthIndicator(Environment environment, MonitorProperties monitorProperties) {
        this.environment = environment;
        this.healthConfig = monitorProperties.getHealth();
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        String serverAddr = environment.getProperty("spring.cloud.nacos.discovery.server-addr",
            environment.getProperty("spring.cloud.nacos.config.server-addr"));
        if (serverAddr == null || serverAddr.isBlank()) {
            builder.unknown().withDetail("message", "Nacos server-addr not configured");
            return;
        }
        String url = "http://" + serverAddr + "/nacos/v1/console/health/readiness";
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
            try {
                conn.setConnectTimeout(healthConfig.getConnectTimeout());
                conn.setReadTimeout(healthConfig.getReadTimeout());
                conn.setRequestMethod("GET");
                int code = conn.getResponseCode();
                if (code == 200) {
                    builder.up().withDetail("serverAddr", serverAddr);
                } else {
                    builder.down().withDetail("serverAddr", serverAddr).withDetail("httpStatus", code);
                }
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            builder.down(e).withDetail("serverAddr", serverAddr);
        }
    }
}
