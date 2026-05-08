package com.sloth.boot.starter.web.config;

import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.security.xss.XssProperties;
import com.sloth.boot.common.security.xss.wrapper.XssHttpServletRequestWrapper;
import com.sloth.boot.starter.web.event.AccessLogEvent;
import com.sloth.boot.starter.web.filter.CachedBodyHttpServletRequestWrapper;
import com.sloth.boot.starter.web.handler.GlobalExceptionHandler;
import com.sloth.boot.starter.web.handler.GlobalResponseAdvice;
import com.sloth.boot.starter.web.interceptor.UserContextInterceptor;
import com.sloth.boot.starter.web.properties.SlothWebProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Web 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties({
        SlothWebProperties.class,
        CorsConfiguration.class
})
@Import(JacksonConfiguration.class)
public class WebAutoConfiguration {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

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
     * 注册 XSS 过滤配置。
     *
     * @param slothWebProperties Web 配置
     * @return XSS 配置
     */
    @Bean
    @ConditionalOnMissingBean
    public XssProperties xssProperties(SlothWebProperties slothWebProperties) {
        XssProperties xssProperties = new XssProperties();
        xssProperties.setEnabled(slothWebProperties.isXssEnabled());
        xssProperties.setExcludeUrls(slothWebProperties.getXssExcludeUrls());
        return xssProperties;
    }

    /**
     * 注册 XSS 过滤器。
     *
     * @param xssProperties XSS 配置
     * @return XSS 过滤器注册器
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothXssFilterRegistration")
    @ConditionalOnProperty(prefix = "sloth.web", name = "xss-enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<OncePerRequestFilter> slothXssFilterRegistration(XssProperties xssProperties) {
        FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                if (shouldSkip(request, xssProperties)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                filterChain.doFilter(new XssHttpServletRequestWrapper(request, xssProperties), response);
            }
        });
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("slothXssFilter");
        registrationBean.setOrder(Integer.MIN_VALUE + 100);
        return registrationBean;
    }

    private boolean shouldSkip(HttpServletRequest request, XssProperties xssProperties) {
        String requestUri = request.getRequestURI();
        if (!xssProperties.isEnabled()) {
            return true;
        }
        for (String excludeUrl : xssProperties.getExcludeUrls()) {
            if (ANT_PATH_MATCHER.match(excludeUrl, requestUri)) {
                return true;
            }
        }
        return false;
    }

    // ==================== 新增特性 ====================

    /**
     * 注册请求体缓存过滤器（支持多次读取 @RequestBody）。
     *
     * @return 请求体缓存过滤器注册器
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothBodyCacheFilterRegistration")
    @ConditionalOnProperty(prefix = "sloth.web", name = "body-cache-enabled", havingValue = "true")
    public FilterRegistrationBean<OncePerRequestFilter> slothBodyCacheFilterRegistration() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                             FilterChain filterChain) throws ServletException, IOException {
                CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);
                filterChain.doFilter(wrappedRequest, response);
            }
        });
        registration.addUrlPatterns("/*");
        registration.setName("slothBodyCacheFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }

    /**
     * 注册 API 访问日志过滤器（发布 AccessLogEvent 事件）。
     *
     * @param eventPublisher   事件发布器
     * @param webProperties    Web 配置
     * @return 访问日志过滤器注册器
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothAccessLogFilterRegistration")
    @ConditionalOnProperty(prefix = "sloth.web", name = "access-log-enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<OncePerRequestFilter> slothAccessLogFilterRegistration(
            ApplicationEventPublisher eventPublisher, SlothWebProperties webProperties) {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                             FilterChain filterChain) throws ServletException, IOException {
                long start = System.currentTimeMillis();
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    long elapsed = System.currentTimeMillis() - start;
                    String requestBody = null;
                    if (webProperties.isBodyCacheEnabled()
                        && request instanceof CachedBodyHttpServletRequestWrapper cached) {
                        requestBody = cached.getCachedBodyAsString();
                    }
                    String clientIp = getClientIp(request);
                    Long userId = null;
                    try {
                        userId = UserContext.getUserId();
                    } catch (Exception ignored) {
                    }
                    AccessLogEvent event = new AccessLogEvent(this,
                        request.getMethod(), request.getRequestURI(), request.getQueryString(),
                        clientIp, request.getHeader("User-Agent"), userId,
                        response.getStatus(), elapsed, requestBody);
                    eventPublisher.publishEvent(event);
                }
            }
        });
        registration.addUrlPatterns("/*");
        registration.setName("slothAccessLogFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
