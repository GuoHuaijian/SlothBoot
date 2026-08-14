package com.sloth.boot.starter.web.filter;

import com.sloth.boot.starter.web.config.WebCorsProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CORS 跨域过滤器。
 * <p>
 * 对允许的 Origin 设置 CORS 响应头（Allow-Origin、Allow-Credentials、Allow-Methods、
 * Allow-Headers、Max-Age），并对 OPTIONS 预检请求直接返回 200。
 * <p>
 * 优先级高于 Sa-Token CORS，通过 {@code Integer.MIN_VALUE + 50} 保证最先执行。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class CorsFilter extends OncePerRequestFilter {

    private final WebCorsProperties corsConfiguration;

    /**
     * 构造 CORS 过滤器。
     *
     * @param corsConfiguration 跨域配置
     */
    public CorsFilter(WebCorsProperties corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }

    /**
     * 执行 CORS 处理。
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
        String origin = request.getHeader("Origin");
        if (origin != null && corsConfiguration.getAllowedOrigins().contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods",
                String.join(",", corsConfiguration.getAllowedMethods()));
            response.setHeader("Access-Control-Allow-Headers",
                String.join(",", corsConfiguration.getAllowedHeaders()));
            response.setHeader("Access-Control-Max-Age", String.valueOf(corsConfiguration.getMaxAge()));
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
