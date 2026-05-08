package com.sloth.boot.example.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.starter.ai.core.AiChatClient;
import com.sloth.boot.starter.ai.core.AiEmbeddingClient;
import com.sloth.boot.starter.ai.core.AiImageClient;
import com.sloth.boot.starter.ai.dto.ChatRequest;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

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

    @Autowired(required = false)
    private AiEmbeddingClient aiEmbeddingClient;

    @Autowired(required = false)
    private AiImageClient aiImageClient;

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

    /**
     * AI 多轮对话示例接口（带记忆）。
     * <p>
     * 需要先开启 sloth.ai.memory.enabled=true。
     * 相同 conversationId 的请求会共享上下文。
     *
     * @param prompt         用户输入内容
     * @param conversationId 会话 ID
     * @return AI 响应（含 Token 用量）
     */
    @GetMapping("/ai/chat/conversation")
    public R<ChatResponse> conversation(@RequestParam("prompt") String prompt,
                                         @RequestParam("conversationId") String conversationId) {
        ChatRequest request = ChatRequest.builder()
            .userPrompt(prompt)
            .conversationId(conversationId)
            .build();
        return R.ok(aiChatClient.chat(request));
    }

    /**
     * AI 结构化输出示例接口。
     * <p>
     * 将 AI 响应自动解析为指定类型（需返回合法 JSON）。
     *
     * @param prompt 用户输入内容
     * @return 解析后的 Map 对象
     */
    @GetMapping("/ai/chat/structured")
    public R<Map<?, ?>> structured(@RequestParam("prompt") String prompt) {
        return R.ok(aiChatClient.chat(prompt, Map.class));
    }

    /**
     * 向量嵌入示例接口。
     * <p>
     * 需要 classpath 中存在 EmbeddingModel（如 OpenAI）。
     *
     * @param text 输入文本
     * @return 嵌入向量
     */
    @GetMapping("/ai/embedding")
    public R<float[]> embedding(@RequestParam("text") String text) {
        if (aiEmbeddingClient == null) {
            return R.fail("向量嵌入功能未启用，请检查 EmbeddingModel 是否在 classpath 中");
        }
        return R.ok(aiEmbeddingClient.embed(text));
    }

    /**
     * 图像生成示例接口。
     * <p>
     * 需要 classpath 中存在 ImageModel（如 OpenAI DALL-E）。
     *
     * @param prompt 图像描述
     * @return 图像 URL
     */
    @GetMapping("/ai/image")
    public R<String> generateImage(@RequestParam("prompt") String prompt) {
        if (aiImageClient == null) {
            return R.fail("图像生成功能未启用，请检查 ImageModel 是否在 classpath 中");
        }
        return R.ok(aiImageClient.generate(prompt));
    }
}
