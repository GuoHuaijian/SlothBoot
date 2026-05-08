package com.sloth.boot.starter.web.config;

import com.sloth.boot.starter.web.handler.GlobalExceptionHandler;
import com.sloth.boot.starter.web.handler.GlobalResponseAdvice;
import com.sloth.boot.starter.web.interceptor.UserContextInterceptor;
import com.sloth.boot.starter.web.properties.SlothWebProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WebAutoConfiguration 条件装配测试")
class WebAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner =
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(WebAutoConfiguration.class));

    @Test
    @DisplayName("默认配置下注册所有核心 Bean")
    void registersAllBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            assertThat(context).hasSingleBean(GlobalResponseAdvice.class);
            assertThat(context).hasSingleBean(UserContextInterceptor.class);
            assertThat(context).hasSingleBean(SlothWebProperties.class);
        });
    }

    @Test
    @DisplayName("SlothWebProperties 默认值正确")
    void defaultPropertiesValues() {
        contextRunner.run(context -> {
            SlothWebProperties props = context.getBean(SlothWebProperties.class);
            assertThat(props.isResponseWrapper()).isTrue();
            assertThat(props.isXssEnabled()).isTrue();
        });
    }

    @Test
    @DisplayName("用户自定义 GlobalExceptionHandler 可覆盖默认")
    void customExceptionHandlerOverridesDefault() {
        contextRunner.withBean("customExceptionHandler", GlobalExceptionHandler.class, GlobalExceptionHandler::new)
            .run(context -> {
                assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
                assertThat(context.getBean("customExceptionHandler")).isNotNull();
            });
    }

    @Test
    @DisplayName("responseWrapper=false 时 GlobalResponseAdvice 仍注册但 supports 返回 false")
    void responseWrapperDisabled() {
        contextRunner.withPropertyValues("sloth.web.response-wrapper=false").run(context -> {
            assertThat(context).hasSingleBean(GlobalResponseAdvice.class);
            SlothWebProperties props = context.getBean(SlothWebProperties.class);
            assertThat(props.isResponseWrapper()).isFalse();
        });
    }
}
