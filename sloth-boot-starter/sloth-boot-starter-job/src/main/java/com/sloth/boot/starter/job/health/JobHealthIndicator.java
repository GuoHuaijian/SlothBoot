package com.sloth.boot.starter.job.health;

import com.sloth.boot.starter.job.config.JobProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;

/**
 * XXL-JOB 健康检查。
 * <p>
 * 报告执行器配置状态。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class JobHealthIndicator extends AbstractHealthIndicator {

    private final JobProperties jobProperties;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        builder.up()
            .withDetail("adminAddresses", jobProperties.getAdminAddresses())
            .withDetail("appname", jobProperties.getAppname())
            .withDetail("address", jobProperties.getAddress());
    }
}
