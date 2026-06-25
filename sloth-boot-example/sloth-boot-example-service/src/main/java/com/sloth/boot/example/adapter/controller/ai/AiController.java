package com.sloth.boot.example.adapter.controller.ai;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.ai.AiCommand;
import com.sloth.boot.starter.ai.core.AiChatClient;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI 演示接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "AI 能力", description = "演示 AI 对话、流式输出、多轮记忆、结构化输出等能力")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@ConditionalOnBean(AiChatClient.class)
public class AiController {

    private final AiCommand aiCommand;

    @Operation(summary = "简单对话", description = "发送提示词，获取AI回复文本")
    @PostMapping("/chat")
    public R<String> chat(@RequestParam String prompt) {
        return R.ok(aiCommand.chat(prompt));
    }

    @Operation(summary = "流式对话（SSE）", description = "逐token返回AI回复，适合打字机效果")
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String prompt) {
        return aiCommand.chatStream(prompt);
    }

    @Operation(summary = "多轮记忆对话", description = "带会话ID的多轮对话，AI会记住历史上下文")
    @PostMapping("/chat/conversation")
    public R<ChatResponse> chatWithMemory(@RequestParam String prompt, @RequestParam String conversationId) {
        return R.ok(aiCommand.chatWithMemory(prompt, conversationId));
    }

    @Operation(summary = "结构化输出", description = "AI将响应解析为结构化JSON格式")
    @PostMapping("/chat/structured")
    public R<Map<String, Object>> chatStructured(@RequestParam String prompt) {
        return R.ok(aiCommand.chatStructured(prompt));
    }
}
