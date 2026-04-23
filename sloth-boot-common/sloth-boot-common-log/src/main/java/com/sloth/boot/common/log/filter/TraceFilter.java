package com.sloth.boot.common.log.filter;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.TraceContext;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Trace 过滤器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class TraceFilter extends OncePerRequestFilter {

    /**
     * 过滤请求并透传 TraceId。
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
        String traceId = request.getHeader(HeaderConstant.TRACE_ID);
        if (StrUtil.isBlank(traceId)) {
            traceId = TraceContext.generateTraceId();
        }

        TraceContext.TraceInfo traceInfo = new TraceContext.TraceInfo();
        traceInfo.setTraceId(traceId);
        TraceContext.set(traceInfo);
        MDC.put("traceId", traceId);
        response.setHeader(HeaderConstant.TRACE_ID, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
            TraceContext.clear();
        }
    }
}
