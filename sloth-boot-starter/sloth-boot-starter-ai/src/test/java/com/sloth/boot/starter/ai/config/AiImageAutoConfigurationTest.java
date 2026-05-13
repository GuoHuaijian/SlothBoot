package com.sloth.boot.starter.ai.config;

import com.sloth.boot.starter.ai.core.AiImageClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImageModel;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("AiImageAutoConfiguration 条件装配测试")
class AiImageAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(AiImageAutoConfiguration.class))
        .withBean(ImageModel.class, () -> mock(ImageModel.class));

    @Test
    @DisplayName("默认配置下注册 AiImageClient")
    void registersImageClientByDefault() {
        contextRunner.run(context -> {
            assertThat(context.getBeansOfType(AiImageClient.class)).isNotEmpty();
        });
    }

    @Test
    @DisplayName("sloth.ai.image.enabled=false 时不注册")
    void disabledByProperty() {
        contextRunner
            .withPropertyValues("sloth.ai.image.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(AiImageClient.class);
            });
    }

    @Test
    @DisplayName("用户自定义 AiImageClient 可覆盖默认")
    void customOverrides() {
        contextRunner
            .withBean("customImageClient", AiImageClient.class,
                () -> mock(AiImageClient.class))
            .run(context -> {
                assertThat(context.getBeansOfType(AiImageClient.class)).isNotEmpty();
            });
    }
}
