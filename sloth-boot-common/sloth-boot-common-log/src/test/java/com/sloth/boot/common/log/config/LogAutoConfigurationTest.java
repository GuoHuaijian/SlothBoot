package com.sloth.boot.common.log.config;

import com.sloth.boot.common.log.OperateLogHandler;
import com.sloth.boot.common.log.event.OperateLogListener;
import com.sloth.boot.common.log.filter.TraceFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("LogAutoConfiguration 条件装配测试")
class LogAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner =
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(LogAutoConfiguration.class));

    @Test
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context.getBeansOfType(TraceFilter.class)).isNotEmpty();
            assertThat(context.getBeansOfType(OperateLogHandler.class)).isNotEmpty();
            assertThat(context.getBeansOfType(OperateLogListener.class)).isNotEmpty();
        });
    }

    @Test
    @DisplayName("sloth.log.enabled=false 时不注册任何 Bean")
    void disabledByProperty() {
        contextRunner.withPropertyValues("sloth.log.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(TraceFilter.class);
            assertThat(context).doesNotHaveBean(OperateLogHandler.class);
            assertThat(context).doesNotHaveBean(OperateLogListener.class);
        });
    }

    @Test
    @DisplayName("用户自定义 OperateLogHandler 可覆盖默认")
    void customOperateLogHandlerOverrides() {
        contextRunner.withBean("customHandler", OperateLogHandler.class, () -> mock(OperateLogHandler.class))
            .run(context -> {
                assertThat(context.getBeansOfType(OperateLogHandler.class)).isNotEmpty();
            });
    }
}
