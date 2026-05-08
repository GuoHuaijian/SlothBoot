package com.sloth.boot.starter.ai.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.starter.ai.dto.ChatRequest;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import com.sloth.boot.starter.ai.support.AiErrorCode;
import com.sloth.boot.starter.ai.support.AiPromptTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

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

    @Override
    public String chat(String userPrompt) {
        validatePrompt(userPrompt);
        return chatClient.prompt()
            .user(userPrompt)
            .call()
            .content();
    }

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

    @Override
    public Flux<String> chatStream(String userPrompt) {
        validatePrompt(userPrompt);
        return chatClient.prompt()
                .user(userPrompt)
                .stream()
                .content();
    }

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

    @Override
    public ChatResponse chat(ChatRequest request) {
        validatePrompt(request.getUserPrompt());
        var spec = buildPromptSpec(request);

        org.springframework.ai.chat.model.ChatResponse springResponse = spec.call().chatResponse();
        return extractChatResponse(springResponse);
    }

    @Override
    public Flux<String> chatStream(ChatRequest request) {
        validatePrompt(request.getUserPrompt());
        var spec = buildPromptSpec(request);
        return spec.stream().content();
    }

    @Override
    public <T> T chat(String userPrompt, Class<T> type) {
        validatePrompt(userPrompt);
        String content = chatClient.prompt()
            .user(userPrompt)
            .call()
            .content();
        return parseStructuredOutput(content, type);
    }

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
     * 构建 ChatClient 请求，处理记忆、工具、变量等。
     */
    private ChatClient.ChatClientRequestSpec buildPromptSpec(ChatRequest request) {
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt();

        // 系统提示词
        if (StringUtils.hasText(request.getSystemPrompt())) {
            spec = spec.system(request.getSystemPrompt());
        }

        // 会话记忆
        if (StringUtils.hasText(request.getConversationId()) && chatMemory != null) {
            spec = spec.advisors(org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(request.getConversationId())
                .build());
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

    private boolean hasPerRequestOptions(ChatRequest request) {
        return StringUtils.hasText(request.getModel())
            || request.getTemperature() != null
            || request.getTopP() != null
            || request.getMaxTokens() != null;
    }

    /**
     * 从 Spring AI ChatResponse 提取为 sloth ChatResponse DTO。
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
     * 解析结构化输出。
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
