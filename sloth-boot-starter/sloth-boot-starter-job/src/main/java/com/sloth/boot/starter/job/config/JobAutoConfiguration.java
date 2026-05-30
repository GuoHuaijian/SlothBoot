package com.sloth.boot.starter.job.config;

import com.sloth.boot.starter.job.health.JobHealthIndicator;
import com.sloth.boot.starter.job.metrics.JobMetrics;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Job 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(XxlJobSpringExecutor.class)
@ConditionalOnProperty(prefix = "sloth.job", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(JobProperties.class)
public class JobAutoConfiguration {

    /**
     * 注册 XXL-Job 执行器。
     *
     * @param jobProperties 配置
     * @param environment   环境
     * @return 执行器
     */
    @Bean
    @ConditionalOnMissingBean
    public XxlJobSpringExecutor xxlJobSpringExecutor(JobProperties jobProperties, Environment environment) {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(jobProperties.getAdminAddresses());
        executor.setAccessToken(jobProperties.getAccessToken());
        executor.setAppname(jobProperties.getAppname() == null || jobProperties.getAppname().isBlank()
            ? environment.getProperty("spring.application.name", "application")
            : jobProperties.getAppname());
        executor.setAddress(jobProperties.getAddress());
        executor.setIp(jobProperties.getIp());
        executor.setPort(jobProperties.getPort());
        executor.setLogPath(jobProperties.getLogPath());
        executor.setLogRetentionDays(jobProperties.getLogRetentionDays());
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(HealthIndicator.class)
    public JobHealthIndicator jobHealthIndicator(JobProperties jobProperties) {
        return new JobHealthIndicator(jobProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(MeterRegistry.class)
    public JobMetrics jobMetrics(MeterRegistry registry) {
        return new JobMetrics(registry);
    }
}
