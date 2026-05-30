package com.sloth.boot.starter.feign.health;

import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Map;

/**
 * Feign 健康检查。
 * <p>
 * 报告已注册的 Feign 客户端数量。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class FeignHealthIndicator extends AbstractHealthIndicator {

    private final Map<String, Object> feignClients;

    public FeignHealthIndicator(Map<String, Object> feignClients) {
        this.feignClients = feignClients;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        builder.up()
            .withDetail("registeredClients", feignClients.size());
        for (String name : feignClients.keySet()) {
            builder.withDetail("client." + name, "registered");
        }
    }
}
