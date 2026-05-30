package com.sloth.boot.starter.ai.core;

import tools.jackson.databind.ObjectMapper;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.starter.ai.dto.ChatRequest;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("SpringAiChatClient 单元测试")
class SpringAiChatClientTest {

    private ChatClient chatClient;
    private ChatClient.ChatClientRequestSpec promptSpec;
    private ChatClient.CallResponseSpec callSpec;
    private ChatClient.StreamResponseSpec streamSpec;
    private ObjectMapper objectMapper;
    private SpringAiChatClient springAiChatClient;

    @BeforeEach
    void setUp() {
        chatClient = mock(ChatClient.class);
        promptSpec = mock(ChatClient.ChatClientRequestSpec.class);
        callSpec = mock(ChatClient.CallResponseSpec.class);
        streamSpec = mock(ChatClient.StreamResponseSpec.class);
        objectMapper = new ObjectMapper();

        when(chatClient.prompt()).thenReturn(promptSpec);
        when(promptSpec.user(anyString())).thenReturn(promptSpec);
        when(promptSpec.system(anyString())).thenReturn(promptSpec);
        when(promptSpec.call()).thenReturn(callSpec);
        when(promptSpec.stream()).thenReturn(streamSpec);
        when(promptSpec.advisors(any(Consumer.class))).thenReturn(promptSpec);

        springAiChatClient = new SpringAiChatClient(chatClient, null, objectMapper);
    }

    @Nested
    @DisplayName("chat 方法")
    class ChatTests {

        @Test
        @DisplayName("同步对话 - 正常调用")
        void chat_withValidPrompt_returnsContent() {
            when(callSpec.content()).thenReturn("你好！");

            String result = springAiChatClient.chat("你好");

            assertThat(result).isEqualTo("你好！");
            verify(chatClient).prompt();
            verify(promptSpec).user("你好");
            verify(promptSpec).call();
        }

        @Test
        @DisplayName("同步对话 - 带系统提示词")
        void chat_withSystemPrompt_returnsContent() {
            when(callSpec.content()).thenReturn("回答");

            String result = springAiChatClient.chat("你是一个专家", "请解释 Java");

            assertThat(result).isEqualTo("回答");
            verify(promptSpec).system("你是一个专家");
            verify(promptSpec).user("请解释 Java");
        }

        @Test
        @DisplayName("同步对话 - 空系统提示词退化为普通调用")
        void chat_withBlankSystemPrompt_fallsBackToSimpleChat() {
            when(callSpec.content()).thenReturn("回答");

            String result = springAiChatClient.chat("", "请解释 Java");

            assertThat(result).isEqualTo("回答");
            verify(promptSpec, never()).system(anyString());
        }

        @Test
        @DisplayName("同步对话 - 空提示词抛出异常")
        void chat_withEmptyPrompt_throwsException() {
            assertThatThrownBy(() -> springAiChatClient.chat(""))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("提示词不能为空");
        }

        @Test
        @DisplayName("同步对话 - null 提示词抛出异常")
        void chat_withNullPrompt_throwsException() {
            assertThatThrownBy(() -> springAiChatClient.chat((String) null))
                .isInstanceOf(BizException.class);
        }
    }

    @Nested
    @DisplayName("chatStream 方法")
    class ChatStreamTests {

        @Test
        @DisplayName("流式对话 - 正常调用")
        void chatStream_withValidPrompt_returnsFlux() {
            when(streamSpec.content()).thenReturn(Flux.just("你", "好", "！"));

            Flux<String> result = springAiChatClient.chatStream("你好");

            List<String> collected = result.collectList().block();
            assertThat(collected).containsExactly("你", "好", "！");
        }

        @Test
        @DisplayName("流式对话 - 空提示词抛出异常")
        void chatStream_withEmptyPrompt_throwsException() {
            assertThatThrownBy(() -> springAiChatClient.chatStream(""))
                .isInstanceOf(BizException.class);
        }
    }

    @Nested
    @DisplayName("chat(ChatRequest) 方法")
    class ChatRequestTests {

        @Test
        @DisplayName("结构化请求 - 返回 ChatResponse")
        void chatRequest_withValidRequest_returnsChatResponse() {
            org.springframework.ai.chat.model.ChatResponse springResponse =
                mock(org.springframework.ai.chat.model.ChatResponse.class);
            org.springframework.ai.chat.model.Generation generation =
                mock(org.springframework.ai.chat.model.Generation.class);
            org.springframework.ai.chat.messages.AssistantMessage assistantMessage =
                new org.springframework.ai.chat.messages.AssistantMessage("回答");

            when(springResponse.getResult()).thenReturn(generation);
            when(generation.getOutput()).thenReturn(assistantMessage);
            when(springResponse.getMetadata()).thenReturn(null);
            when(callSpec.chatResponse()).thenReturn(springResponse);

            ChatRequest request = ChatRequest.builder()
                .userPrompt("你好")
                .build();
            ChatResponse response = springAiChatClient.chat(request);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).isEqualTo("回答");
        }

        @Test
        @DisplayName("结构化请求 - 带会话记忆")
        @SuppressWarnings("unchecked")
        void chatRequest_withConversationId_usesMemory() {
            ChatMemory chatMemory = mock(ChatMemory.class);
            SpringAiChatClient clientWithMemory = new SpringAiChatClient(chatClient, chatMemory, objectMapper);

            org.springframework.ai.chat.model.ChatResponse springResponse =
                mock(org.springframework.ai.chat.model.ChatResponse.class);
            org.springframework.ai.chat.model.Generation generation =
                mock(org.springframework.ai.chat.model.Generation.class);
            org.springframework.ai.chat.messages.AssistantMessage assistantMessage =
                new org.springframework.ai.chat.messages.AssistantMessage("回答");

            when(springResponse.getResult()).thenReturn(generation);
            when(generation.getOutput()).thenReturn(assistantMessage);
            when(springResponse.getMetadata()).thenReturn(null);
            when(callSpec.chatResponse()).thenReturn(springResponse);

            ChatRequest request = ChatRequest.builder()
                .userPrompt("你好")
                .conversationId("conv-123")
                .build();
            clientWithMemory.chat(request);

            verify(promptSpec).advisors(any(Consumer.class));
        }
    }

    @Nested
    @DisplayName("结构化输出")
    class StructuredOutputTests {

        @Test
        @DisplayName("chat(String, Class) - 解析为 Map")
        @SuppressWarnings("unchecked")
        void chatWithType_parsesToMap() {
            when(callSpec.content()).thenReturn("{\"name\":\"test\"}");

            Map<String, Object> result = (Map<String, Object>) springAiChatClient.chat("返回 JSON", Map.class);

            assertThat(result).containsEntry("name", "test");
        }

        @Test
        @DisplayName("chat(String, Class) - String 类型直接返回")
        void chatWithType_stringType_returnsDirectly() {
            when(callSpec.content()).thenReturn("普通文本");

            String result = springAiChatClient.chat("你好", String.class);

            assertThat(result).isEqualTo("普通文本");
        }

        @Test
        @DisplayName("chat(String, Class) - 无效 JSON 抛出异常")
        void chatWithType_invalidJson_throwsException() {
            when(callSpec.content()).thenReturn("这不是JSON");

            assertThatThrownBy(() -> springAiChatClient.chat("返回 JSON", Map.class))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("结构化输出");
        }
    }
}
