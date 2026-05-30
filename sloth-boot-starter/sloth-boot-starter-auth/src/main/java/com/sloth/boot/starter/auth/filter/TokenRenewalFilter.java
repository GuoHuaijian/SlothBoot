package com.sloth.boot.starter.auth.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sloth.boot.starter.auth.config.AuthProperties;

/**
 * Token 续期过滤器。
 * <p>
 * 当配置了 activeTimeout > 0 时，每次请求自动续期 Token， 实现滑动窗口过期效果。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class TokenRenewalFilter extends OncePerRequestFilter {

    private final AuthProperties authProperties;

    /**
     * 执行 Token 续期：若用户已登录且配置了活跃超时，则自动续期 Token。
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            if (StpUtil.isLogin() && authProperties.getActiveTimeout() > 0) {
                StpUtil.renewTimeout(authProperties.getActiveTimeout());
            }
        } catch (Exception ignored) {
            // 续期失败不影响请求处理
        }
        filterChain.doFilter(request, response);
    }
}
