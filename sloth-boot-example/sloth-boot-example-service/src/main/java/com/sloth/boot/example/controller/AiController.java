package com.sloth.boot.example.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.service.AiDemoService;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@ConditionalOnBean(com.sloth.boot.starter.ai.core.AiChatClient.class)
public class AiController {

    private final AiDemoService aiDemoService;

    @PostMapping("/chat")
    public R<String> chat(@RequestParam String prompt) {
        return R.ok(aiDemoService.chat(prompt));
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String prompt) {
        return aiDemoService.chatStream(prompt);
    }

    @PostMapping("/chat/conversation")
    public R<ChatResponse> chatConversation(@RequestParam String prompt,
                                            @RequestParam String conversationId) {
        return R.ok(aiDemoService.chatWithMemory(prompt, conversationId));
    }

    @PostMapping("/chat/structured")
    public R<Map<String, Object>> chatStructured(@RequestParam String prompt) {
        return R.ok(aiDemoService.chatStructured(prompt));
    }
}
