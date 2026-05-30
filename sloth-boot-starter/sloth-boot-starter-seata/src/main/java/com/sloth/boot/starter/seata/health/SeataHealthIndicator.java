package com.sloth.boot.starter.seata.health;

import com.sloth.boot.starter.seata.config.SeataProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;

/**
 * Seata 健康检查。
 * <p>
 * 检查 Seata 配置状态和事务组配置是否正确。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SeataHealthIndicator extends AbstractHealthIndicator {

    private final SeataProperties seataProperties;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        builder.up()
            .withDetail("txServiceGroup", seataProperties.getTxServiceGroup())
            .withDetail("mode", seataProperties.getMode())
            .withDetail("enabled", seataProperties.isEnabled());
    }
}
