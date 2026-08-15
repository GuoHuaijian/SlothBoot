package com.sloth.boot.starter.web.filter;

import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.starter.web.config.WebProperties;
import com.sloth.boot.starter.web.event.AccessLogEvent;
import com.sloth.boot.starter.web.util.ServletUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * API 访问日志事件过滤器。
 * <p>
 * 请求完成后发布 {@link AccessLogEvent} 事件，记录请求方法、URI、耗时、客户端 IP 等信息。
 * 业务方可通过 {@code @EventListener(AccessLogEvent.class)} 实现日志持久化。
 * <p>
 * 需配合 {@code sloth.web.access-log-event-enabled=true} 启用。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class AccessLogEventFilter extends OncePerRequestFilter {

    private final ApplicationEventPublisher eventPublisher;
    private final WebProperties webProperties;

    /**
     * 构造访问日志事件过滤器。
     *
     * @param eventPublisher 事件发布器
     * @param webProperties  Web 配置
     */
    public AccessLogEventFilter(ApplicationEventPublisher eventPublisher, WebProperties webProperties) {
        this.eventPublisher = eventPublisher;
        this.webProperties = webProperties;
    }

    /**
     * 执行访问日志事件发布。
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
            String clientIp = ServletUtil.getClientIp(request);
            Long userId = null;
            try {
                userId = UserContext.getUserId();
            } catch (Exception e) {
                log.trace("获取用户上下文失败, 跳过用户ID记录", e);
            }
            AccessLogEvent event = new AccessLogEvent(request.getMethod(), request.getRequestURI(),
                request.getQueryString(), clientIp, request.getHeader("User-Agent"), userId,
                response.getStatus(), elapsed, requestBody);
            eventPublisher.publishEvent(event);
        }
    }
}
