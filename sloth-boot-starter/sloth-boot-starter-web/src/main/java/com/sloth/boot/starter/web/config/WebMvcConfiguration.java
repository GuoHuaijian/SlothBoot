package com.sloth.boot.starter.web.config;

import com.sloth.boot.starter.web.interceptor.UserContextInterceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvc 配置
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final UserContextInterceptor userContextInterceptor;

    /**
     * 注册拦截器。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor).addPathPatterns("/**").excludePathPatterns("/error",
            "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**", "/doc.html");
    }

    /**
     * 注册参数解析器。
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 预留业务自定义参数解析器扩展点。
    }

    /**
     * 跨域配置已通过 {@link com.sloth.boot.starter.web.filter.CorsFilter} 全局过滤器实现，
     * 此处不再重复配置以避免 CORS 头重复。
     */
}
