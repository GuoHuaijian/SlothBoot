package com.sloth.boot.starter.gateway.filter;

import com.sloth.boot.starter.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * IP 黑名单过滤器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class BlackListGlobalFilter implements GlobalFilter, Ordered {

    private final GatewayProperties gatewayProperties;

    /**
     * 构造函数。
     *
     * @param gatewayProperties Gateway 配置
     */
    public BlackListGlobalFilter(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 执行黑名单校验。
     *
     * @param exchange 请求上下文
     * @param chain    过滤器链
     * @return Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String hostAddress = exchange.getRequest().getRemoteAddress() == null
                ? null
                : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        if (hostAddress != null && gatewayProperties.getBlackList().contains(hostAddress)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
