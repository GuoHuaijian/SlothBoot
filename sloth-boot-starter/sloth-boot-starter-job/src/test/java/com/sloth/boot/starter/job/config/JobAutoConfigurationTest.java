package com.sloth.boot.starter.job.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Job 自动配置测试。
 */
class JobAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(JobAutoConfiguration.class));

    @Test
    void should_register_executor_when_enabled() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(XxlJobSpringExecutor.class);
            assertThat(context).hasSingleBean(JobProperties.class);
        });
    }

    @Test
    void should_not_register_when_disabled() {
        contextRunner
            .withPropertyValues("sloth.job.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(XxlJobSpringExecutor.class));
    }

    @Test
    void should_use_application_name_as_default_appname() {
        contextRunner
            .withPropertyValues("spring.application.name=test-app")
            .run(context -> {
                XxlJobSpringExecutor executor = context.getBean(XxlJobSpringExecutor.class);
                assertThat(executor).isNotNull();
            });
    }
}
