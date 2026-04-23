package com.sloth.boot.starter.web.config;

import com.sloth.boot.starter.web.interceptor.UserContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
    private final CorsConfiguration corsConfiguration;

    /**
     * 注册拦截器。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**", "/doc.html");
    }

    /**
     * 注册参数解析器。
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 预留业务自定义参数解析器扩展点。
    }

    /**
     * 配置跨域。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = corsConfiguration.getAllowedOrigins().isEmpty()
                ? new String[]{"*"}
                : corsConfiguration.getAllowedOrigins().toArray(new String[0]);
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods(corsConfiguration.getAllowedMethods().toArray(new String[0]))
                .allowedHeaders(corsConfiguration.getAllowedHeaders().toArray(new String[0]))
                .allowCredentials(corsConfiguration.isAllowCredentials())
                .maxAge(corsConfiguration.getMaxAge());
    }
}
