package com.sloth.boot.starter.monitor.config;

import com.sloth.boot.starter.monitor.metrics.BusinessMetrics;
import com.sloth.boot.starter.monitor.metrics.JvmMetricsConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("MonitorAutoConfiguration 条件装配测试")
class MonitorAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(MonitorAutoConfiguration.class))
            .withBean(MeterRegistry.class, SimpleMeterRegistry::new);

    @Test
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RestTemplate.class);
            assertThat(context).hasSingleBean(JvmMetricsConfig.class);
            assertThat(context).hasSingleBean(BusinessMetrics.class);
        });
    }

    @Test
    @DisplayName("sloth.monitor.enabled=false 时不注册任何 Bean")
    void disabledByProperty() {
        contextRunner.withPropertyValues("sloth.monitor.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(RestTemplate.class);
            assertThat(context).doesNotHaveBean(JvmMetricsConfig.class);
            assertThat(context).doesNotHaveBean(BusinessMetrics.class);
        });
    }

    @Test
    @DisplayName("用户自定义 RestTemplate 可覆盖默认")
    void customRestTemplateOverrides() {
        contextRunner.withBean("customRestTemplate", RestTemplate.class, () -> mock(RestTemplate.class))
            .run(context -> {
                assertThat(context).hasSingleBean(RestTemplate.class);
            });
    }
}
