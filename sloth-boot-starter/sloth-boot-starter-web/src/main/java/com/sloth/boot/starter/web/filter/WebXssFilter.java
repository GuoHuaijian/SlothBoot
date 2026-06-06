package com.sloth.boot.starter.web.filter;

import com.sloth.boot.common.security.xss.XssProperties;
import com.sloth.boot.common.security.xss.wrapper.XssHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * XSS 过滤器。
 * <p>
 * 对非排除路径的请求进行 XSS 参数清洗，委托 {@link XssHttpServletRequestWrapper} 实现。
 * 支持通过 {@link XssProperties#isEnabled()} 全局开关控制，
 * 并支持 Ant 风格的 URL 排除匹配。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class WebXssFilter extends OncePerRequestFilter {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final XssProperties xssProperties;

    /**
     * 构造 XSS 过滤器。
     *
     * @param xssProperties XSS 配置
     */
    public WebXssFilter(XssProperties xssProperties) {
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(new XssHttpServletRequestWrapper(request, xssProperties), response);
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (!xssProperties.isEnabled()) {
            return true;
        }
        Set<String> excludeUrls = xssProperties.getExcludeUrls();
        for (String excludeUrl : excludeUrls) {
            if (ANT_PATH_MATCHER.match(excludeUrl, requestUri)) {
                return true;
            }
        }
        return false;
    }
}
