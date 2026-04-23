package com.sloth.boot.common.security.xss;

import com.sloth.boot.common.security.xss.wrapper.XssHttpServletRequestWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * XSS 过滤器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class XssFilter extends OncePerRequestFilter {

    private final XssProperties xssProperties;

    /**
     * 构造 XSS 过滤器。
     *
     * @param xssProperties XSS 配置
     */
    public XssFilter(XssProperties xssProperties) {
        this.xssProperties = xssProperties;
    }

    /**
     * 执行 XSS 过滤。
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!xssProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(new XssHttpServletRequestWrapper(request, xssProperties), response);
    }
}
