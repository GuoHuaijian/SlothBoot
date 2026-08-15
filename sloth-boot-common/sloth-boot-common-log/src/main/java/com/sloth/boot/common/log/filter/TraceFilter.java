package com.sloth.boot.common.log.filter;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.log.config.LogProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Trace 过滤器。
 * <p>
 * 覆盖 {@code OncePerRequestFilter} 的守卫机制，使用独立的请求属性控制执行次数。
 * 正常请求结束后守卫立即清除，使得错误派发时 TraceFilter 能再次执行，
 * 从请求属性恢复 traceId 到 MDC，确保异常处理链中的日志都携带链路ID。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class TraceFilter extends OncePerRequestFilter {

    private static final String TRACE_GUARD = TraceFilter.class.getName() + ".GUARD";

    private static final int MAX_TRACE_ID_LENGTH = 64;

    private static final java.util.regex.Pattern TRACE_ID_PATTERN =
            java.util.regex.Pattern.compile("[0-9a-zA-Z-]{1," + MAX_TRACE_ID_LENGTH + "}");

    private final LogProperties logProperties;

    /**
     * 构造 Trace 过滤器。
     *
     * @param logProperties 日志配置
     */
    public TraceFilter(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    private final org.springframework.util.AntPathMatcher pathMatcher = new org.springframework.util.AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (request.getAttribute(TRACE_GUARD) != null) {
            return true;
        }
        String requestUri = request.getRequestURI();
        for (String excludeUrl : logProperties.getExcludeUrls()) {
            if (pathMatcher.match(excludeUrl, requestUri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        request.setAttribute(TRACE_GUARD, Boolean.TRUE);

        // 错误派发：请求属性中已有 traceId，恢复到 MDC 与 TraceContext
        String existing = (String) request.getAttribute(HeaderConstant.TRACE_ID);
        if (existing != null) {
            restoreContext(existing);
            try {
                filterChain.doFilter(request, response);
            } finally {
                MDC.remove(HeaderConstant.MDC_TRACE_ID);
                TraceContext.clear();
            }
            return;
        }

        // 正常请求：生成 traceId，非法入站值不信任，重新生成
        String traceId = request.getHeader(HeaderConstant.TRACE_ID);
        if (!isValidTraceId(traceId)) {
            traceId = TraceContext.generateTraceId();
        }

        TraceContext.TraceInfo traceInfo = new TraceContext.TraceInfo();
        traceInfo.setTraceId(traceId);
        TraceContext.set(traceInfo);
        MDC.put(HeaderConstant.MDC_TRACE_ID, traceId);
        request.setAttribute(HeaderConstant.TRACE_ID, traceId);
        response.setHeader(HeaderConstant.TRACE_ID, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            request.removeAttribute(TRACE_GUARD);
            MDC.remove(HeaderConstant.MDC_TRACE_ID);
            TraceContext.clear();
        }
    }

    private void restoreContext(String traceId) {
        MDC.put(HeaderConstant.MDC_TRACE_ID, traceId);
        TraceContext.TraceInfo traceInfo = new TraceContext.TraceInfo();
        traceInfo.setTraceId(traceId);
        TraceContext.set(traceInfo);
    }

    private boolean isValidTraceId(String traceId) {
        return !StrUtil.isBlank(traceId) && TRACE_ID_PATTERN.matcher(traceId).matches();
    }
}
