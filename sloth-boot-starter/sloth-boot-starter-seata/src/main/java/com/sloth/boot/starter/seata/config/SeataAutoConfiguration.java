package com.sloth.boot.starter.seata.config;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.starter.seata.health.SeataHealthIndicator;
import com.sloth.boot.starter.seata.metrics.SeataMetrics;
import com.sloth.boot.starter.seata.tracing.SeataTracingFilter;
import io.micrometer.core.instrument.MeterRegistry;
import io.seata.spring.annotation.GlobalTransactionScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Seata 自动配置。
 * <p>
 * 注册 {@link GlobalTransactionScanner}、{@link SeataHealthIndicator}、{@link SeataMetrics}、
 * {@link SeataTracingFilter}，支持条件装配。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(GlobalTransactionScanner.class)
@ConditionalOnProperty(prefix = "sloth.seata", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SeataProperties.class)
public class SeataAutoConfiguration {

    /**
     * 注册全局事务扫描器。
     *
     * @param environment     Spring 环境
     * @param seataProperties Seata 配置
     * @return 全局事务扫描器
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalTransactionScanner globalTransactionScanner(Environment environment, SeataProperties seataProperties) {
        String applicationName = environment.getProperty("spring.application.name", "sloth-boot-app");
        String txServiceGroup = resolveTxServiceGroup(applicationName, seataProperties.getTxServiceGroup());
        log.info("[Seata] 初始化 Seata 全局事务扫描器, applicationName={}, txServiceGroup={}, mode={}", applicationName, txServiceGroup,
            seataProperties.getMode());
        return new GlobalTransactionScanner(applicationName, txServiceGroup);
    }

    @Bean
    @ConditionalOnMissingBean
    public SeataHealthIndicator seataHealthIndicator(SeataProperties seataProperties) {
        return new SeataHealthIndicator(seataProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(MeterRegistry.class)
    public SeataMetrics seataMetrics(MeterRegistry registry) {
        return new SeataMetrics(registry);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public SeataTracingFilter seataTracingFilter() {
        return new SeataTracingFilter();
    }

    private String resolveTxServiceGroup(String applicationName, String txServiceGroup) {
        if (StrUtil.isBlank(txServiceGroup)) {
            return applicationName + "-tx-group";
        }
        return txServiceGroup.replace("${spring.application.name}", applicationName);
    }
}
