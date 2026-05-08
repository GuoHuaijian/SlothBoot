package com.sloth.boot.starter.ai.support;

import com.sloth.boot.common.decorator.LoggingDecorator;
import com.sloth.boot.starter.ai.config.AiProperties;
import com.sloth.boot.starter.ai.core.AiChatClient;
import com.sloth.boot.starter.ai.dto.ChatRequest;
import com.sloth.boot.starter.ai.dto.ChatResponse;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * AI 对话客户端可观测性装饰器。
 * <p>
 * 自动记录请求/响应日志、耗时统计、Token 用量。
 * 流式方法通过 Reactor 操作符实现异步日志。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class AiChatClientDecorator extends LoggingDecorator<AiChatClient> implements AiChatClient {

    private final AiProperties.Observability observability;

    public AiChatClientDecorator(AiChatClient target, AiProperties.Observability observability) {
        super(target);
        this.observability = observability;
    }

    // ==================== 同步方法 ====================

    @Override
    public String chat(String userPrompt) {
        return execute(() -> target.chat(userPrompt), "chat", userPrompt);
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        return execute(() -> target.chat(systemPrompt, userPrompt),
            "chat(system,user)", systemPrompt, userPrompt);
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        return execute(() -> target.chat(request), "chat(request)", request);
    }

    @Override
    public <T> T chat(String userPrompt, Class<T> type) {
        return execute(() -> target.chat(userPrompt, type),
            "chat(user,type)", userPrompt);
    }

    @Override
    public <T> T chat(String systemPrompt, String userPrompt, Class<T> type) {
        return execute(() -> target.chat(systemPrompt, userPrompt, type),
            "chat(system,user,type)", systemPrompt, userPrompt);
    }

    // ==================== 流式方法 ====================

    @Override
    public Flux<String> chatStream(String userPrompt) {
        return wrapStream(target.chatStream(userPrompt), "chatStream", userPrompt);
    }

    @Override
    public Flux<String> chatStream(String systemPrompt, String userPrompt) {
        return wrapStream(target.chatStream(systemPrompt, userPrompt),
            "chatStream(system,user)", systemPrompt, userPrompt);
    }

    @Override
    public Flux<String> chatStream(ChatRequest request) {
        return wrapStream(target.chatStream(request), "chatStream(request)", request);
    }

    // ==================== 钩子方法 ====================

    @Override
    protected void after(String methodName, Object result, long elapsed) {
        if (elapsed > observability.getSlowThresholdMs()) {
            log.warn("[AI] {} 慢调用, 耗时: {}ms", methodName, elapsed);
        } else {
            log.debug("[AI] {} 完成, 耗时: {}ms", methodName, elapsed);
        }
        if (result instanceof ChatResponse chatResponse && chatResponse.getTotalTokens() != null) {
            log.debug("[AI] {} Token 用量: 输入={}, 输出={}, 总计={}",
                methodName, chatResponse.getPromptTokens(),
                chatResponse.getCompletionTokens(), chatResponse.getTotalTokens());
        }
    }

    @Override
    protected void onError(String methodName, Throwable throwable, long elapsed) {
        log.error("[AI] {} 失败, 耗时: {}ms, 错误: {}",
            methodName, elapsed, throwable.getMessage(), throwable);
    }

    /**
     * 为流式方法添加日志和耗时统计。
     */
    private Flux<String> wrapStream(Flux<String> source, String methodName, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug("[AI] {} 开始流式调用", methodName);
        }
        long start = System.currentTimeMillis();
        return source
            .doOnComplete(() -> {
                long elapsed = System.currentTimeMillis() - start;
                if (elapsed > observability.getSlowThresholdMs()) {
                    log.warn("[AI] {} 流式完成, 耗时: {}ms", methodName, elapsed);
                } else {
                    log.debug("[AI] {} 流式完成, 耗时: {}ms", methodName, elapsed);
                }
            })
            .doOnError(throwable -> {
                long elapsed = System.currentTimeMillis() - start;
                log.error("[AI] {} 流式失败, 耗时: {}ms, 错误: {}",
                    methodName, elapsed, throwable.getMessage(), throwable);
            });
    }
}
