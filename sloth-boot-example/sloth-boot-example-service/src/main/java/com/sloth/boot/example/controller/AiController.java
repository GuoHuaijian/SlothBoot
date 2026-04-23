package com.sloth.boot.example.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.starter.ai.core.AiChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
