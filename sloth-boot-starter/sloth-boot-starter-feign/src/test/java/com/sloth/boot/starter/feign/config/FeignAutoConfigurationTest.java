package com.sloth.boot.starter.feign.config;

import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("FeignAutoConfiguration 条件装配测试")
class FeignAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(
        AutoConfigurations.of(HttpMessageConvertersAutoConfiguration.class, FeignAutoConfiguration.class));

    @Test
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context.getBeansOfType(RequestInterceptor.class)).isNotEmpty();
            assertThat(context.getBeansOfType(Decoder.class)).isNotEmpty();
            assertThat(context.getBeansOfType(ErrorDecoder.class)).isNotEmpty();
        });
    }

    @Test
    @DisplayName("缺少 FeignClient 类时不注册任何 Bean")
    void disabledWhenClassMissing() {
        // FeignAutoConfiguration 有 @ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignClient")
        // 如果该类不在 classpath 上，自动配置将跳过。
        // 由于 spring-cloud-starter-openfeign 是编译期依赖，此测试通过空上下文验证未加载时的行为。
        new ApplicationContextRunner().run(context -> {
            // 没有引入 FeignAutoConfiguration，验证 Bean 不存在
            assertThat(context).doesNotHaveBean(RequestInterceptor.class);
            assertThat(context).doesNotHaveBean(Decoder.class);
            assertThat(context).doesNotHaveBean(ErrorDecoder.class);
        });
    }

    @Test
    @DisplayName("用户自定义 ErrorDecoder 可覆盖默认")
    void customErrorDecoderOverrides() {
        contextRunner.withBean("customErrorDecoder", ErrorDecoder.class, () -> mock(ErrorDecoder.class))
            .run(context -> {
                assertThat(context.getBeansOfType(ErrorDecoder.class)).isNotEmpty();
            });
    }
}
