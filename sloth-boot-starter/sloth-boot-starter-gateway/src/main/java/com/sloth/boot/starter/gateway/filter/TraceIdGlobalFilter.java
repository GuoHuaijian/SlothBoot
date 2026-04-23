package com.sloth.boot.starter.gateway.filter;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.TraceContext;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * TraceId 全局过滤器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class TraceIdGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 注入 TraceId 到下游请求头。
     *
     * @param exchange 请求上下文
     * @param chain    过滤器链
     * @return Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst(HeaderConstant.TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = TraceContext.generateTraceId();
        }
        String finalTraceId = traceId;
        ServerWebExchange mutated = exchange.mutate()
                .request(builder -> builder.header(HeaderConstant.TRACE_ID, finalTraceId))
                .build();
        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
