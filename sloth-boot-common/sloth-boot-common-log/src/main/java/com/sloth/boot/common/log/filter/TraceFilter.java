package com.sloth.boot.common.log.filter;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.TraceContext;
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getAttribute(TRACE_GUARD) != null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        request.setAttribute(TRACE_GUARD, Boolean.TRUE);

        // 错误派发：请求属性中已有 traceId，恢复到 MDC
        String existing = (String) request.getAttribute(HeaderConstant.TRACE_ID);
        if (existing != null) {
            MDC.put(HeaderConstant.MDC_TRACE_ID, existing);
            try {
                filterChain.doFilter(request, response);
            } finally {
                MDC.remove(HeaderConstant.MDC_TRACE_ID);
            }
            return;
        }

        // 正常请求：生成 traceId
        String traceId = request.getHeader(HeaderConstant.TRACE_ID);
        if (StrUtil.isBlank(traceId)) {
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
}
