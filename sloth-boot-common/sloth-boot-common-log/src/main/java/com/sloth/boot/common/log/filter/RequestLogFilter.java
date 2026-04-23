package com.sloth.boot.common.log.filter;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.log.properties.LogProperties;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.common.util.ServletUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志过滤器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RequestLogFilter extends OncePerRequestFilter {

    private final LogProperties logProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 是否跳过日志。
     *
     * @param request 请求
     * @return 是否跳过
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return logProperties.getExcludeUrls().stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestUri));
    }

    /**
     * 执行日志记录。
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
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, response);
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            if (logProperties.isPrintRequestLog()) {
                log.info("RequestLog: {}", JsonUtil.toJson(buildRequestLog(requestWrapper)));
            }
            if (logProperties.isPrintResponseLog()) {
                Map<String, Object> responseLog = new HashMap<>(4);
                responseLog.put("status", response.getStatus());
                responseLog.put("costTime", costTime);
                responseLog.put("traceId", TraceContext.getTraceId());
                log.info("ResponseLog: {}", JsonUtil.toJson(responseLog));
            }
        }
    }

    private Map<String, Object> buildRequestLog(ContentCachingRequestWrapper request) {
        Map<String, Object> requestLog = new HashMap<>(8);
        requestLog.put("method", request.getMethod());
        requestLog.put("url", request.getRequestURL().toString());
        requestLog.put("query", request.getQueryString());
        requestLog.put("clientIp", ServletUtil.getClientIp(request));
        requestLog.put("userAgent", request.getHeader("User-Agent"));
        requestLog.put("traceId", TraceContext.getTraceId());
        requestLog.put("headers", extractHeaders(request));
        requestLog.put("body", extractBody(request));
        return requestLog;
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>(8);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (StrUtil.equalsIgnoreCase(headerName, HeaderConstant.TOKEN)) {
                headers.put(headerName, StrUtil.hide(headerValue, 4, Math.max(4, StrUtil.length(headerValue) - 4)));
            } else {
                headers.put(headerName, headerValue);
            }
        }
        return headers;
    }

    private String extractBody(ContentCachingRequestWrapper request) {
        byte[] body = request.getContentAsByteArray();
        if (body.length == 0) {
            return null;
        }
        String bodyText = new String(body, StandardCharsets.UTF_8);
        if (bodyText.length() > logProperties.getMaxBodyLength()) {
            return bodyText.substring(0, logProperties.getMaxBodyLength()) + "...";
        }
        return bodyText;
    }
}
