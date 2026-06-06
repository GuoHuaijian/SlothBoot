package com.sloth.boot.starter.web.config;

import com.sloth.boot.common.event.EventPublisher;
import com.sloth.boot.common.log.config.LogAutoConfiguration;
import com.sloth.boot.common.security.config.SecurityAutoConfiguration;
import com.sloth.boot.starter.web.handler.GlobalExceptionHandler;
import com.sloth.boot.starter.web.handler.GlobalResponseAdvice;
import com.sloth.boot.starter.web.interceptor.UserContextInterceptor;
import com.sloth.boot.starter.web.config.SlothWebProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WebAutoConfiguration 条件装配测试")
class WebAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner =
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LogAutoConfiguration.class, SecurityAutoConfiguration.class, WebAutoConfiguration.class))
            .withBean(EventPublisher.class, () -> new EventPublisher(event -> {}));

    @Test
    @DisplayName("默认配置下注册所有核心 Bean")
    void registersAllBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context.getBeansOfType(GlobalExceptionHandler.class)).isNotEmpty();
            assertThat(context.getBeansOfType(GlobalResponseAdvice.class)).isNotEmpty();
            assertThat(context.getBeansOfType(UserContextInterceptor.class)).isNotEmpty();
            assertThat(context.getBeansOfType(SlothWebProperties.class)).isNotEmpty();
        });
    }

    @Test
    @DisplayName("SlothWebProperties 默认值正确")
    void defaultPropertiesValues() {
        contextRunner.run(context -> {
            SlothWebProperties props = context.getBeansOfType(SlothWebProperties.class).values().iterator().next();
            assertThat(props.isResponseWrapper()).isTrue();
            assertThat(props.isBodyCacheEnabled()).isFalse();
        });
    }

    @Test
    @DisplayName("用户自定义 GlobalExceptionHandler 可覆盖默认")
    void customExceptionHandlerOverridesDefault() {
        contextRunner.withBean("customExceptionHandler", GlobalExceptionHandler.class, GlobalExceptionHandler::new)
            .run(context -> {
                assertThat(context.getBeansOfType(GlobalExceptionHandler.class)).isNotEmpty();
                assertThat(context.getBean("customExceptionHandler")).isNotNull();
            });
    }

    @Test
    @DisplayName("responseWrapper=false 时 GlobalResponseAdvice 仍注册但 supports 返回 false")
    void responseWrapperDisabled() {
        contextRunner.withPropertyValues("sloth.web.response-wrapper=false").run(context -> {
            assertThat(context.getBeansOfType(GlobalResponseAdvice.class)).isNotEmpty();
            SlothWebProperties props = context.getBeansOfType(SlothWebProperties.class).values().iterator().next();
            assertThat(props.isResponseWrapper()).isFalse();
        });
    }
}
