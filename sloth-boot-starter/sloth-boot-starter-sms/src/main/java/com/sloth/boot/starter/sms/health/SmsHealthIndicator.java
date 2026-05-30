package com.sloth.boot.starter.sms.health;

import com.sloth.boot.starter.sms.config.SmsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;

/**
 * 短信服务健康检查。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SmsHealthIndicator extends AbstractHealthIndicator {

    private final SmsProperties smsProperties;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        builder.up()
            .withDetail("provider", smsProperties.getType())
            .withDetail("region", smsProperties.getRegionId())
            .withDetail("signName", smsProperties.getSignName())
            .withDetail("credentialsConfigured",
                smsProperties.getAccessKeyId() != null && !smsProperties.getAccessKeyId().isEmpty());
    }
}
