package com.sloth.boot.starter.gateway.handler;

import com.sloth.boot.common.result.R;
import com.sloth.boot.common.util.JsonUtil;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Gateway Sentinel 降级处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class SentinelFallbackHandler {

    /**
     * 返回统一降级响应。
     *
     * @param exchange 请求上下文
     * @return Mono
     */
    public Mono<Void> handle(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = JsonUtil.toJson(R.fail(429, "网关限流或降级触发")).getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }
}
