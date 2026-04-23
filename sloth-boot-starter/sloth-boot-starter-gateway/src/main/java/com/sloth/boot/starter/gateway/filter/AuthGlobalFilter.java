package com.sloth.boot.starter.gateway.filter;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.starter.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 认证全局过滤器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String DEFAULT_INNER_CALL_VALUE = "false";
    private static final String DEFAULT_USER_ID = "0";
    private static final String DEFAULT_USERNAME = "gateway";

    private final GatewayProperties gatewayProperties;

    /**
     * 构造函数。
     *
     * @param gatewayProperties Gateway 配置
     */
    public AuthGlobalFilter(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 执行认证与用户透传。
     *
     * @param exchange 请求上下文
     * @param chain    过滤器链
     * @return Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst(HeaderConstant.TOKEN);
        if (token == null || token.isBlank()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
        builder.header(HeaderConstant.INNER_CALL, DEFAULT_INNER_CALL_VALUE);
        builder.header(
                HeaderConstant.USER_ID,
                defaultHeader(exchange, HeaderConstant.USER_ID, DEFAULT_USER_ID)
        );
        builder.header(
                HeaderConstant.USERNAME,
                defaultHeader(exchange, HeaderConstant.USERNAME, DEFAULT_USERNAME)
        );
        if (exchange.getRequest().getHeaders().getFirst(HeaderConstant.TENANT_ID) != null) {
            builder.header(HeaderConstant.TENANT_ID, exchange.getRequest().getHeaders().getFirst(HeaderConstant.TENANT_ID));
        }
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    private String defaultHeader(ServerWebExchange exchange, String headerName, String defaultValue) {
        String headerValue = exchange.getRequest().getHeaders().getFirst(headerName);
        return headerValue == null ? defaultValue : headerValue;
    }

    private boolean isWhiteList(String path) {
        return gatewayProperties.getWhiteList().stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
