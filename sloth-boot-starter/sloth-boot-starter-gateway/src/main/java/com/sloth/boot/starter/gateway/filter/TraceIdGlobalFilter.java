package com.sloth.boot.starter.gateway.filter;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.TraceContext;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * TraceId 全局过滤器。
 * <p>
 * 将 TraceId 注入下游请求头，并桥接到 SLF4J MDC，
 * 使 Gateway 端日志也包含 traceId。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class TraceIdGlobalFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_KEY = "traceId";

    /**
     * 注入 TraceId 到下游请求头，并通过 Reactor Context 传播到 MDC。
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
        return chain.filter(mutated)
            .contextWrite(Context.of(TRACE_ID_KEY, finalTraceId))
            .doOnEach(signal -> {
                if (!signal.isOnError()) {
                    String ctxTraceId = signal.getContextView().getOrDefault(TRACE_ID_KEY, null);
                    if (ctxTraceId != null) {
                        MDC.put(TRACE_ID_KEY, ctxTraceId);
                    }
                }
            })
            .doFinally(signal -> MDC.clear());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
