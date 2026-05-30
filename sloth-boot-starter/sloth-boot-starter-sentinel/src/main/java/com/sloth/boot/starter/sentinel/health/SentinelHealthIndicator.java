package com.sloth.boot.starter.sentinel.health;

import com.sloth.boot.starter.sentinel.config.SentinelProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;

/**
 * Sentinel 健康检查。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SentinelHealthIndicator extends AbstractHealthIndicator {

    private final SentinelProperties sentinelProperties;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        builder.up()
            .withDetail("datasource", sentinelProperties.getDatasource())
            .withDetail("enabled", sentinelProperties.isEnabled());
    }
}
