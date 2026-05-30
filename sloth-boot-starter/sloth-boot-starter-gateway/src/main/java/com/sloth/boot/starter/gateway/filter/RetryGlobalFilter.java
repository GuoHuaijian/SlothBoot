package com.sloth.boot.starter.gateway.filter;

import com.sloth.boot.starter.gateway.config.GatewayProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * 全局重试过滤器。
 * <p>
 * 当后端服务返回 5xx 错误时，自动进行重试。支持配置最大重试次数和退避间隔。 仅当
 * {@code sloth.gateway.retry.enabled=true} 时生效。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class RetryGlobalFilter implements GlobalFilter, Ordered {

    private static final Retry5xxException RETRY_MARKER = new Retry5xxException();

    private final GatewayProperties gatewayProperties;

    /**
     * 构造函数。
     *
     * @param gatewayProperties Gateway 配置
     */
    public RetryGlobalFilter(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 执行重试过滤逻辑。
     * <p>
     * 在过滤器链执行完成后检查响应状态码，若为 5xx 则根据配置进行重试。
     *
     * @param exchange 请求上下文
     * @param chain    过滤器链
     * @return Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayProperties.RetryConfig retryConfig = gatewayProperties.getRetry();
        if (retryConfig == null || !retryConfig.isEnabled()) {
            return chain.filter(exchange);
        }
        int maxAttempts = retryConfig.getMaxAttempts();
        long backoffMs = retryConfig.getBackoffMs();
        String path = exchange.getRequest().getPath().value();
        return chain.filter(exchange).then(Mono.defer(() -> {
            HttpStatusCode status = exchange.getResponse().getStatusCode();
            if (status != null && status.is5xxServerError()) {
                log.warn("[Gateway] 请求 {} 返回 {}，触发重试", path, status.value());
                return Mono.error(RETRY_MARKER);
            }
            return Mono.empty();
        })).retryWhen(Retry.backoff(maxAttempts, Duration.ofMillis(backoffMs)).filter(RETRY_MARKER::equals)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure())).then();
    }

    /**
     * 获取过滤器顺序。
     * <p>
     * 优先级较高（数值较小），确保在响应提交前检查状态码。
     *
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return -5;
    }

    /**
     * 5xx 重试标记异常。
     */
    private static class Retry5xxException extends RuntimeException {
        Retry5xxException() {
            super("Gateway 5xx retry marker");
        }
    }
}
