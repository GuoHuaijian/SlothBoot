package com.sloth.boot.starter.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 请求体缓存过滤器。
 * <p>
 * 将请求包装为 {@link CachedBodyHttpServletRequestWrapper}，使 {@code @RequestBody}
 * 和日志 Filter 等多方均可多次读取请求体内容。
 * <p>
 * 需配合 {@code sloth.web.body-cache-enabled=true} 启用。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class BodyCacheFilter extends OncePerRequestFilter {

    /**
     * 执行请求体缓存包装。
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);
        filterChain.doFilter(wrappedRequest, response);
    }
}
