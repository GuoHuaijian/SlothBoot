package com.sloth.boot.starter.auth.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.starter.auth.properties.AuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Token 续期过滤器。
 * <p>
 * 当配置了 activeTimeout > 0 时，每次请求自动续期 Token，
 * 实现滑动窗口过期效果。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class TokenRenewalFilter extends OncePerRequestFilter {

    private final AuthProperties authProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
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
