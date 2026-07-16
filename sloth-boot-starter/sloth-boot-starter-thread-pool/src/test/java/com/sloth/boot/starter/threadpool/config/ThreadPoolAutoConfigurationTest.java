package com.sloth.boot.starter.threadpool.config;

import com.sloth.boot.starter.threadpool.async.AsyncExceptionHandler;
import com.sloth.boot.starter.threadpool.core.ThreadPoolRegistry;
import com.sloth.boot.starter.threadpool.core.VisibleThreadPoolExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("ThreadPoolAutoConfiguration 条件装配测试")
class ThreadPoolAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ThreadPoolAutoConfiguration.class));

    @Test
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context.getBeansOfType(ThreadPoolRegistry.class)).isNotEmpty();
            assertThat(context).hasBean("slothTaskExecutor");
            assertThat(context.getBeansOfType(VisibleThreadPoolExecutor.class)).isNotEmpty();
            assertThat(context.getBeansOfType(AsyncExceptionHandler.class)).isNotEmpty();
            assertThat(context.getBeansOfType(ScheduledThreadPoolExecutor.class)).isNotEmpty();
        });
    }

    @Test
    @DisplayName("sloth.thread-pool.enabled=false 时不注册任何 Bean")
    void disabledByProperty() {
        contextRunner.withPropertyValues("sloth.thread-pool.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(ThreadPoolRegistry.class);
            assertThat(context).doesNotHaveBean("slothTaskExecutor");
            assertThat(context).doesNotHaveBean(AsyncExceptionHandler.class);
            assertThat(context).doesNotHaveBean(ScheduledThreadPoolExecutor.class);
        });
    }

    @Test
    @DisplayName("用户自定义 ThreadPoolRegistry 可覆盖默认")
    void customThreadPoolRegistryOverrides() {
        contextRunner.withBean("customRegistry", ThreadPoolRegistry.class, () -> mock(ThreadPoolRegistry.class))
            .run(context -> {
                assertThat(context.getBeansOfType(ThreadPoolRegistry.class)).isNotEmpty();
            });
    }
}
