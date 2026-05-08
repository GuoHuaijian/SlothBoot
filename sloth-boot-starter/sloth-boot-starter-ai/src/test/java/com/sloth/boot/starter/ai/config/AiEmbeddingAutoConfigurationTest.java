package com.sloth.boot.starter.ai.config;

import com.sloth.boot.starter.ai.core.AiEmbeddingClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("AiEmbeddingAutoConfiguration 条件装配测试")
class AiEmbeddingAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(AiEmbeddingAutoConfiguration.class))
        .withBean(EmbeddingModel.class, () -> mock(EmbeddingModel.class));

    @Test
    @DisplayName("默认配置下注册 AiEmbeddingClient")
    void registersEmbeddingClientByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AiEmbeddingClient.class);
        });
    }

    @Test
    @DisplayName("sloth.ai.embedding.enabled=false 时不注册")
    void disabledByProperty() {
        contextRunner
            .withPropertyValues("sloth.ai.embedding.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(AiEmbeddingClient.class);
            });
    }

    @Test
    @DisplayName("用户自定义 AiEmbeddingClient 可覆盖默认")
    void customOverrides() {
        contextRunner
            .withBean("customEmbeddingClient", AiEmbeddingClient.class,
                () -> mock(AiEmbeddingClient.class))
            .run(context -> {
                assertThat(context).hasSingleBean(AiEmbeddingClient.class);
            });
    }
}
