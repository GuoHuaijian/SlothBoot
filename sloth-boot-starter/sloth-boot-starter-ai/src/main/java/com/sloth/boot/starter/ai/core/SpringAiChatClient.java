package com.sloth.boot.starter.ai.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.starter.ai.dto.ChatRequest;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import com.sloth.boot.starter.ai.support.AiErrorCode;
import com.sloth.boot.starter.ai.support.AiPromptTemplate;

/**
 * 基于 Spring AI ChatClient 的统一对话客户端。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SpringAiChatClient implements AiChatClient {

    private final ChatClient chatClient;

    private final ChatMemory chatMemory;

    private final ObjectMapper objectMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public String chat(String userPrompt) {
        validatePrompt(userPrompt);
        return chatClient.prompt()
            .user(userPrompt)
            .call()
            .content();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String chat(String systemPrompt, String userPrompt) {
        validatePrompt(userPrompt);
        if (StringUtils.hasText(systemPrompt)) {
            return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
        }
        return chat(userPrompt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<String> chatStream(String userPrompt) {
        validatePrompt(userPrompt);
        return chatClient.prompt()
            .user(userPrompt)
            .stream()
            .content();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<String> chatStream(String systemPrompt, String userPrompt) {
        validatePrompt(userPrompt);
        if (StringUtils.hasText(systemPrompt)) {
            return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .stream()
                .content();
        }
        return chatStream(userPrompt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatResponse chat(ChatRequest request) {
        validatePrompt(request.getUserPrompt());
        var spec = buildPromptSpec(request);

        org.springframework.ai.chat.model.ChatResponse springResponse = spec.call().chatResponse();
        return extractChatResponse(springResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<String> chatStream(ChatRequest request) {
        validatePrompt(request.getUserPrompt());
        var spec = buildPromptSpec(request);
        return spec.stream().content();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T chat(String userPrompt, Class<T> type) {
        validatePrompt(userPrompt);
        String content = chatClient.prompt()
            .user(userPrompt)
            .call()
            .content();
        return parseStructuredOutput(content, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T chat(String systemPrompt, String userPrompt, Class<T> type) {
        validatePrompt(userPrompt);
        String content;
        if (StringUtils.hasText(systemPrompt)) {
            content = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
        } else {
            content = chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
        }
        return parseStructuredOutput(content, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T chatStructured(ChatRequest request, Class<T> clazz) {
        validatePrompt(request.getUserPrompt());
        BeanOutputConverter<T> converter = new BeanOutputConverter<>(clazz);
        // 将格式指令追加到用户提示词末尾，确保经过模板渲染和工具注册等完整流程
        ChatRequest structuredRequest =
            ChatRequest.builder().userPrompt(request.getUserPrompt() + "\n\n" + converter.getFormat())
                .systemPrompt(request.getSystemPrompt()).conversationId(request.getConversationId())
                .model(request.getModel()).temperature(request.getTemperature()).topP(request.getTopP())
                .maxTokens(request.getMaxTokens()).variables(request.getVariables()).tools(request.getTools()).build();
        var spec = buildPromptSpec(structuredRequest);
        String content = spec.call().content();
        return converter.convert(content);
    }

    /**
     * 构建 ChatClient 请求，处理记忆、工具、变量模板渲染及每请求参数覆盖。
     *
     * @param request 对话请求
     * @return ChatClient 请求规格
     */
    private ChatClient.ChatClientRequestSpec buildPromptSpec(ChatRequest request) {
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt();

        // 系统提示词
        if (StringUtils.hasText(request.getSystemPrompt())) {
            spec = spec.system(request.getSystemPrompt());
        }

        // 会话记忆
        if (StringUtils.hasText(request.getConversationId()) && chatMemory != null) {
            spec = spec.advisors(a -> a
                .param("conversationId", request.getConversationId())
                .advisors(org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor.builder(chatMemory).build()));
        }

        // 用户消息（支持变量模板渲染）
        String userMessage = request.getUserPrompt();
        if (request.getVariables() != null && !request.getVariables().isEmpty()) {
            userMessage = AiPromptTemplate.render(userMessage, request.getVariables());
        }
        spec = spec.user(userMessage);

        // 工具注册
        if (request.getTools() != null && !request.getTools().isEmpty()) {
            spec = spec.tools(request.getTools().toArray());
        }

        // 每请求参数覆盖
        if (hasPerRequestOptions(request)) {
            OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder();
            if (StringUtils.hasText(request.getModel())) {
                optionsBuilder.model(request.getModel());
            }
            if (request.getTemperature() != null) {
                optionsBuilder.temperature(request.getTemperature());
            }
            if (request.getTopP() != null) {
                optionsBuilder.topP(request.getTopP());
            }
            if (request.getMaxTokens() != null) {
                optionsBuilder.maxTokens(request.getMaxTokens());
            }
            spec = spec.options(optionsBuilder.build());
        }

        return spec;
    }

    /**
     * 判断请求是否包含每请求级别的参数覆盖。
     *
     * @param request 对话请求
     * @return 是否有参数覆盖
     */
    private boolean hasPerRequestOptions(ChatRequest request) {
        return StringUtils.hasText(request.getModel())
            || request.getTemperature() != null
            || request.getTopP() != null
            || request.getMaxTokens() != null;
    }

    /**
     * 从 Spring AI ChatResponse 提取为 Sloth ChatResponse DTO。
     *
     * @param springResponse Spring AI 响应对象
     * @return Sloth 对话响应
     */
    private ChatResponse extractChatResponse(org.springframework.ai.chat.model.ChatResponse springResponse) {
        ChatResponse.ChatResponseBuilder builder = ChatResponse.builder()
            .content(springResponse.getResult().getOutput().getText());

        if (springResponse.getMetadata() != null) {
            Usage usage = springResponse.getMetadata().getUsage();
            if (usage != null) {
                builder.promptTokens(usage.getPromptTokens() != null ? usage.getPromptTokens().longValue() : null)
                    .completionTokens(usage.getCompletionTokens() != null ? usage.getCompletionTokens().longValue() : null)
                    .totalTokens(usage.getTotalTokens() != null ? usage.getTotalTokens().longValue() : null);
            }
            builder.model(springResponse.getMetadata().getModel());
            if (springResponse.getResult().getMetadata() != null
                && springResponse.getResult().getMetadata().getFinishReason() != null) {
                builder.finishReason(springResponse.getResult().getMetadata().getFinishReason());
            }
        }
        return builder.build();
    }

    /**
     * 将响应文本解析为指定类型的对象。
     *
     * @param content 响应文本
     * @param type    目标类型
     * @param <T>     泛型
     * @return 解析后的对象实例
     */
    private <T> T parseStructuredOutput(String content, Class<T> type) {
        if (type == String.class) {
            return type.cast(content);
        }
        try {
            return objectMapper.readValue(content, type);
        } catch (Exception e) {
            throw BizException.of(AiErrorCode.STRUCTURED_OUTPUT_ERROR);
        }
    }

    /**
     * 校验用户提示词。
     *
     * @param userPrompt 用户提示词
     */
    private void validatePrompt(String userPrompt) {
        if (!StringUtils.hasText(userPrompt)) {
            throw BizException.of(AiErrorCode.EMPTY_PROMPT);
        }
    }
}
