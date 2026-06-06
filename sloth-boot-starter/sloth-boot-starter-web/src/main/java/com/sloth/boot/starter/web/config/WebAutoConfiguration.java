package com.sloth.boot.starter.web.config;

import com.sloth.boot.common.event.EventPublisher;
import com.sloth.boot.common.log.config.LogProperties;
import com.sloth.boot.common.security.xss.XssProperties;
import com.sloth.boot.starter.web.filter.AccessLogEventFilter;
import com.sloth.boot.starter.web.filter.BodyCacheFilter;
import com.sloth.boot.starter.web.filter.CorsFilter;
import com.sloth.boot.starter.web.filter.WebXssFilter;
import com.sloth.boot.starter.web.handler.GlobalExceptionHandler;
import com.sloth.boot.starter.web.handler.GlobalResponseAdvice;
import com.sloth.boot.starter.web.interceptor.UserContextInterceptor;
import com.sloth.boot.starter.web.log.OperateLogAspect;
import com.sloth.boot.starter.web.log.RequestLogFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Web 自动配置。
 * <p>
 * 注册 {@link UserContextInterceptor}、{@link WebMvcConfiguration}、{@link GlobalExceptionHandler}、
 * {@link GlobalResponseAdvice}、{@link RequestLogFilter}、{@link OperateLogAspect}、
 * XSS 过滤器、CORS 过滤器、请求体缓存过滤器、访问日志事件过滤器，支持条件装配。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(HttpServletRequest.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "sloth.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({SlothWebProperties.class, CorsConfiguration.class, GzipProperties.class})
public class WebAutoConfiguration {

    /**
     * 注册用户上下文拦截器。
     *
     * @return 用户上下文拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public UserContextInterceptor userContextInterceptor() {
        return new UserContextInterceptor();
    }

    /**
     * 注册 WebMvc 配置。
     *
     * @param userContextInterceptor 用户上下文拦截器
     * @param corsConfiguration      跨域配置
     * @return WebMvc 配置
     */
    @Bean
    @ConditionalOnMissingBean
    public WebMvcConfiguration webMvcConfiguration(UserContextInterceptor userContextInterceptor,
                                                   CorsConfiguration corsConfiguration) {
        return new WebMvcConfiguration(userContextInterceptor, corsConfiguration);
    }

    /**
     * 注册全局异常处理器。
     *
     * @return 全局异常处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * 注册统一响应包装处理器。
     *
     * @param slothWebProperties Web 配置
     * @return 统一响应包装处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalResponseAdvice globalResponseAdvice(SlothWebProperties slothWebProperties) {
        return new GlobalResponseAdvice(slothWebProperties);
    }

    /**
     * 注册请求日志过滤器。
     *
     * @param logProperties 日志配置
     * @return 请求日志过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestLogFilter requestLogFilter(LogProperties logProperties) {
        return new RequestLogFilter(logProperties);
    }

    /**
     * 注册操作日志切面。
     *
     * @param eventPublisher 事件发布器
     * @return 操作日志切面
     */
    @Bean
    @ConditionalOnMissingBean
    public OperateLogAspect operateLogAspect(EventPublisher eventPublisher) {
        return new OperateLogAspect(eventPublisher);
    }

    /**
     * 注册 XSS 过滤器。
     *
     * @param xssProperties XSS 配置（由 SecurityAutoConfiguration 注册）
     * @return XSS 过滤器注册器
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothXssFilterRegistration")
    @ConditionalOnProperty(prefix = "sloth.xss", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<WebXssFilter> slothXssFilterRegistration(XssProperties xssProperties) {
        FilterRegistrationBean<WebXssFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new WebXssFilter(xssProperties));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("slothXssFilter");
        registrationBean.setOrder(Integer.MIN_VALUE + 100);
        return registrationBean;
    }

    /**
     * 全局 CORS 过滤器（最高优先级，覆盖 Sa-Token CORS）。
     *
     * @param corsConfiguration 跨域配置
     * @return CORS 过滤器注册器
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothCorsFilterRegistration")
    public FilterRegistrationBean<CorsFilter> slothCorsFilterRegistration(CorsConfiguration corsConfiguration) {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorsFilter(corsConfiguration));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("slothCorsFilter");
        registrationBean.setOrder(Integer.MIN_VALUE + 50);
        return registrationBean;
    }

    /**
     * 注册请求体缓存过滤器（支持多次读取 @RequestBody）。
     *
     * @return 请求体缓存过滤器注册器
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothBodyCacheFilterRegistration")
    @ConditionalOnProperty(prefix = "sloth.web", name = "body-cache-enabled", havingValue = "true")
    public FilterRegistrationBean<BodyCacheFilter> slothBodyCacheFilterRegistration() {
        FilterRegistrationBean<BodyCacheFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new BodyCacheFilter());
        registration.addUrlPatterns("/*");
        registration.setName("slothBodyCacheFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }

    /**
     * 注册 API 访问日志事件过滤器（发布 AccessLogEvent 事件）。
     * <p>
     * 默认关闭，避免与 common-log 的 RequestLogFilter 重复记录。
     * 如需事件驱动的日志持久化，可通过 sloth.web.access-log-event-enabled=true 开启。
     *
     * @param eventPublisher 事件发布器
     * @param webProperties  Web 配置
     * @return 访问日志过滤器注册器
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothAccessLogFilterRegistration")
    @ConditionalOnProperty(prefix = "sloth.web", name = "access-log-event-enabled", havingValue = "true")
    public FilterRegistrationBean<AccessLogEventFilter> slothAccessLogFilterRegistration(
        ApplicationEventPublisher eventPublisher, SlothWebProperties webProperties) {
        FilterRegistrationBean<AccessLogEventFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AccessLogEventFilter(eventPublisher, webProperties));
        registration.addUrlPatterns("/*");
        registration.setName("slothAccessLogFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }

}
