package com.sloth.boot.starter.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求日志全局过滤器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class RequestLogGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 记录请求日志。
     *
     * @param exchange 请求上下文
     * @param chain    过滤器链
     * @return Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        return chain.filter(exchange)
                .doFinally(signalType -> log.info("Gateway request, method={}, path={}, query={}, clientIp={}, cost={}ms",
                        exchange.getRequest().getMethod(),
                        exchange.getRequest().getPath().value(),
                        exchange.getRequest().getURI().getQuery(),
                        exchange.getRequest().getRemoteAddress(),
                        System.currentTimeMillis() - startTime));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
