package com.sloth.boot.starter.ai.core;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.starter.ai.support.AiErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.StringUtils;

/**
 * 基于 Spring AI ChatClient 的统一对话客户端。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SpringAiChatClient implements AiChatClient {

    private final ChatClient chatClient;

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
