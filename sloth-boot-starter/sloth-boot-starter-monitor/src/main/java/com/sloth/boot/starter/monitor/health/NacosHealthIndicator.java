package com.sloth.boot.starter.monitor.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.core.env.Environment;

/**
 * Nacos 健康检查器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class NacosHealthIndicator extends AbstractHealthIndicator {

    private final Environment environment;

    /**
     * 构造函数。
     *
     * @param environment Spring 环境
     */
    public NacosHealthIndicator(Environment environment) {
        this.environment = environment;
    }

    /**
     * 执行健康检查。
     *
     * @param builder Health 构建器
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        String serverAddr = environment.getProperty("spring.cloud.nacos.discovery.server-addr",
                environment.getProperty("spring.cloud.nacos.config.server-addr"));
        if (serverAddr == null || serverAddr.isBlank()) {
            builder.unknown().withDetail("message", "未配置 Nacos 地址");
            return;
        }
        builder.up().withDetail("serverAddr", serverAddr);
    }
}
