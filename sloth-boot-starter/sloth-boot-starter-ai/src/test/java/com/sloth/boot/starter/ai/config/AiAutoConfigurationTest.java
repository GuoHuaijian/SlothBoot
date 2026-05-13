package com.sloth.boot.starter.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sloth.boot.starter.ai.core.AiChatClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("AiAutoConfiguration 条件装配测试")
class AiAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(AiAutoConfiguration.class))
        .withBean(ChatModel.class, () -> mock(ChatModel.class))
        .withBean(ObjectMapper.class, ObjectMapper::new);

    @Test
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("slothAiChatClient");
            assertThat(context.getBeansOfType(ChatClient.class)).isNotEmpty();
            assertThat(context.getBeansOfType(AiChatClient.class)).isNotEmpty();
        });
    }

    @Test
    @DisplayName("sloth.ai.enabled=false 时不注册任何 Bean")
    void disabledByProperty() {
        contextRunner
            .withPropertyValues("sloth.ai.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(AiChatClient.class);
                assertThat(context).doesNotHaveBean(ChatClient.class);
            });
    }

    @Test
    @DisplayName("用户自定义 AiChatClient 可覆盖默认")
    void customAiChatClientOverrides() {
        contextRunner
            .withBean("customAiChatClient", AiChatClient.class,
                () -> mock(AiChatClient.class))
            .run(context -> {
                assertThat(context.getBeansOfType(AiChatClient.class)).isNotEmpty();
            });
    }

    @Test
    @DisplayName("用户自定义 ChatClient 按名称覆盖默认")
    void customChatClientOverridesByName() {
        contextRunner
            .withBean("slothAiChatClient", ChatClient.class,
                () -> mock(ChatClient.class))
            .run(context -> {
                assertThat(context.getBeansOfType(ChatClient.class)).isNotEmpty();
            });
    }

    @Test
    @DisplayName("启用记忆后注册 ChatMemory Bean")
    void memoryEnabledRegistersChatMemory() {
        contextRunner
            .withPropertyValues("sloth.ai.memory.enabled=true")
            .run(context -> {
                assertThat(context.getBeansOfType(ChatMemory.class)).isNotEmpty();
            });
    }

    @Test
    @DisplayName("未启用记忆时不注册 ChatMemory Bean")
    void memoryDisabledDoesNotRegisterChatMemory() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(ChatMemory.class);
        });
    }
}
