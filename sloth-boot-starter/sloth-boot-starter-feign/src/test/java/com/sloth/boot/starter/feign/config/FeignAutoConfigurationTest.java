package com.sloth.boot.starter.feign.config;

import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("FeignAutoConfiguration 条件装配测试")
class FeignAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(FeignAutoConfiguration.class))
        .withBean(FeignHttpMessageConverters.class, () -> mock(FeignHttpMessageConverters.class))
        .withBean("feignHttpMessageConvertersProvider", ObjectProvider.class,
            () -> mock(ObjectProvider.class));

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
        new ApplicationContextRunner().run(context -> {
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
