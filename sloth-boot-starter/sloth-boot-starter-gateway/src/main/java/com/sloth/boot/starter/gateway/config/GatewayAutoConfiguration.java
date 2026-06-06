package com.sloth.boot.starter.gateway.config;

import com.sloth.boot.starter.gateway.filter.BlackListGlobalFilter;
import com.sloth.boot.starter.gateway.filter.HeaderForwardingFilter;
import com.sloth.boot.starter.gateway.filter.RequestLogGlobalFilter;
import com.sloth.boot.starter.gateway.filter.RetryGlobalFilter;
import com.sloth.boot.starter.gateway.filter.TraceIdGlobalFilter;
import com.sloth.boot.starter.gateway.handler.GatewayExceptionHandler;
import com.sloth.boot.starter.gateway.handler.SentinelFallbackHandler;
import com.sloth.boot.starter.gateway.route.DynamicRouteService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * Gateway 自动配置。
 * <p>
 * 注册 {@link TraceIdGlobalFilter}、{@link HeaderForwardingFilter}、{@link RequestLogGlobalFilter}、
 * {@link BlackListGlobalFilter}、{@link GatewayExceptionHandler}、{@link SentinelFallbackHandler}、
 * {@link DynamicRouteService}、{@link RetryGlobalFilter}，支持条件装配。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(RouteDefinitionWriter.class)
@ConditionalOnProperty(prefix = "sloth.gateway", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(GatewayProperties.class)
@Import(CorsConfig.class)
public class GatewayAutoConfiguration {

    /**
     * 注册 Trace 过滤器。
     *
     * @return 过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public TraceIdGlobalFilter traceIdGlobalFilter() {
        return new TraceIdGlobalFilter();
    }

    /**
     * 注册认证过滤器。
     *
     * @param gatewayProperties Gateway 配置
     * @return 过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public HeaderForwardingFilter headerForwardingFilter(GatewayProperties gatewayProperties) {
        return new HeaderForwardingFilter(gatewayProperties);
    }

    /**
     * 注册请求日志过滤器。
     *
     * @return 过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestLogGlobalFilter requestLogGlobalFilter() {
        return new RequestLogGlobalFilter();
    }

    /**
     * 注册黑名单过滤器。
     *
     * @param gatewayProperties Gateway 配置
     * @return 过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public BlackListGlobalFilter blackListGlobalFilter(GatewayProperties gatewayProperties) {
        return new BlackListGlobalFilter(gatewayProperties);
    }

    /**
     * 注册网关异常处理器。
     *
     * @return 异常处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public GatewayExceptionHandler gatewayExceptionHandler() {
        return new GatewayExceptionHandler();
    }

    /**
     * 注册 Sentinel 降级处理器（仅当 Sentinel 在 classpath 上时生效）。
     *
     * @return 降级处理器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "com.alibaba.csp.sentinel.SphU")
    public SentinelFallbackHandler sentinelFallbackHandler() {
        return new SentinelFallbackHandler();
    }

    /**
     * 注册动态路由服务。
     *
     * @param routeDefinitionWriter RouteDefinitionWriter
     * @param gatewayProperties     Gateway 配置
     * @param environment           环境
     * @return 动态路由服务
     */
    @Bean
    @ConditionalOnProperty(prefix = "sloth.gateway", name = "dynamic-route-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public DynamicRouteService dynamicRouteService(RouteDefinitionWriter routeDefinitionWriter,
                                                   GatewayProperties gatewayProperties,
                                                   Environment environment) {
        return new DynamicRouteService(routeDefinitionWriter, gatewayProperties, environment);
    }

    /**
     * 注册全局重试过滤器（仅当 {@code sloth.gateway.retry.enabled=true} 时生效）。
     *
     * @param gatewayProperties Gateway 配置
     * @return 重试过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.gateway.retry", name = "enabled", havingValue = "true")
    public RetryGlobalFilter retryGlobalFilter(GatewayProperties gatewayProperties) {
        return new RetryGlobalFilter(gatewayProperties);
    }
}
