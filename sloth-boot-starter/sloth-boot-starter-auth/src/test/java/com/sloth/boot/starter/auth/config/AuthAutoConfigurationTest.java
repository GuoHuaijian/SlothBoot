package com.sloth.boot.starter.auth.config;

import com.sloth.boot.starter.auth.handler.SaTokenContextHandler;
import com.sloth.boot.starter.auth.listener.SaTokenEventListener;
import com.sloth.boot.starter.auth.service.DefaultPermissionService;
import com.sloth.boot.starter.auth.service.OnlineUserService;
import com.sloth.boot.starter.auth.service.PermissionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("AuthAutoConfiguration 条件装配测试")
class AuthAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner =
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(AuthAutoConfiguration.class));

    @Test
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SaTokenContextHandler.class);
            assertThat(context).hasSingleBean(SaTokenEventListener.class);
            assertThat(context).hasSingleBean(OnlineUserService.class);
            assertThat(context).hasSingleBean(PermissionService.class);
            assertThat(context).hasSingleBean(DefaultPermissionService.class);
            // authWebMvcConfigurer 是匿名 WebMvcConfigurer
            assertThat(context).getBean("authWebMvcConfigurer").isInstanceOf(WebMvcConfigurer.class);
        });
    }

    @Test
    @DisplayName("sloth.auth.enabled=false 时不注册任何 Bean")
    void disabledByProperty() {
        contextRunner.withPropertyValues("sloth.auth.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(SaTokenContextHandler.class);
            assertThat(context).doesNotHaveBean(SaTokenEventListener.class);
            assertThat(context).doesNotHaveBean(OnlineUserService.class);
            assertThat(context).doesNotHaveBean(PermissionService.class);
            assertThat(context).doesNotHaveBean("authWebMvcConfigurer");
        });
    }

    @Test
    @DisplayName("用户自定义 PermissionService 可覆盖默认")
    void customPermissionServiceOverrides() {
        contextRunner.withBean("customPermissionService", PermissionService.class, () -> mock(PermissionService.class))
            .run(context -> {
                assertThat(context).hasSingleBean(PermissionService.class);
                assertThat(context).doesNotHaveBean(DefaultPermissionService.class);
            });
    }
}
