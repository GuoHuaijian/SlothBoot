package com.sloth.boot.example.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.starter.ai.core.AiChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * AI 示例控制器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@ConditionalOnBean(AiChatClient.class)
public class AiController {

    private final AiChatClient aiChatClient;

    /**
     * AI 对话示例接口。
     *
     * @param prompt 用户输入内容
     * @return AI 响应文本
     */
    @GetMapping("/ai/chat")
    public R<String> chat(@RequestParam("prompt") String prompt) {
        return R.ok(aiChatClient.chat(prompt));
    }

    /**
     * AI 流式对话示例接口（SSE）。
     * <p>
     * 返回 text/event-stream 格式，逐 token 推送 AI 响应。
     * 适用于实时对话、打字机效果等场景。
     *
     * @param prompt 用户输入内容
     * @return AI 响应文本流
     */
    @GetMapping(value = "/ai/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam("prompt") String prompt) {
        return aiChatClient.chatStream(prompt);
    }
}
