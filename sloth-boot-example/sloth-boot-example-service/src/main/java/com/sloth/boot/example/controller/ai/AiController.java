package com.sloth.boot.example.controller.ai;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.service.ai.AiDemoService;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI 能力演示接口
 * <p>
 * 演示 Spring AI 集成：同步对话、SSE 流式输出、多轮对话记忆、结构化输出
 */
@Tag(name = "AI 能力", description = "演示 Spring AI 集成：对话、流式输出、多轮对话、结构化输出")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@ConditionalOnBean(com.sloth.boot.starter.ai.core.AiChatClient.class)
public class AiController {

    private final AiDemoService aiDemoService;

    @Operation(summary = "AI 对话", description = "发送提示词进行一次简单对话")
    @Parameter(name = "prompt", description = "用户输入的提示词", required = true, example = "介绍一下 Spring Boot")
    @PostMapping("/chat")
    public R<String> chat(@RequestParam @NotBlank(message = "prompt不能为空") String prompt) {
        return R.ok(aiDemoService.chat(prompt));
    }

    @Operation(summary = "AI 流式对话", description = "以 SSE 方式逐 token 返回流式响应")
    @Parameter(name = "prompt", description = "用户输入的提示词", required = true, example = "用 Java 写一个快排")
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String prompt) {
        return aiDemoService.chatStream(prompt);
    }

    @Operation(summary = "AI 多轮对话", description = "带会话记忆的多轮对话，通过 conversationId 维持上下文")
    @Parameter(name = "prompt", description = "用户输入的提示词", required = true)
    @Parameter(name = "conversationId", description = "会话ID，用于维持对话上下文", required = true, example = "conv-001")
    @PostMapping("/chat/conversation")
    public R<ChatResponse> chatConversation(@RequestParam @NotBlank(message = "prompt不能为空") String prompt,
                                            @RequestParam String conversationId) {
        return R.ok(aiDemoService.chatWithMemory(prompt, conversationId));
    }

    @Operation(summary = "AI 结构化输出", description = "将 AI 响应解析为结构化 Map 格式")
    @Parameter(name = "prompt", description = "用户输入的提示词", required = true, example = "列出3个Java框架")
    @PostMapping("/chat/structured")
    public R<Map<String, Object>> chatStructured(@RequestParam @NotBlank(message = "prompt不能为空") String prompt) {
        return R.ok(aiDemoService.chatStructured(prompt));
    }
}
