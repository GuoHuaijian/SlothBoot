package com.sloth.boot.starter.feign.config;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 请求拦截器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class FeignRequestInterceptor implements RequestInterceptor {

    /**
     * 透传上下文请求头。
     *
     * @param requestTemplate Feign 请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = currentRequest();
        copyIfPresent(request, requestTemplate, HeaderConstant.TOKEN);
        copyIfPresent(request, requestTemplate, HeaderConstant.INNER_CALL);

        addHeaderIfPresent(requestTemplate, HeaderConstant.TRACE_ID, TraceContext.getTraceId());
        addHeaderIfPresent(requestTemplate, HeaderConstant.USER_ID,
            UserContext.getUserId() == null ? null : String.valueOf(UserContext.getUserId()));
        addHeaderIfPresent(requestTemplate, HeaderConstant.USERNAME, UserContext.getUsername());
        addHeaderIfPresent(requestTemplate, HeaderConstant.TENANT_ID, UserContext.getTenantId());
    }

    /**
     * 从原始请求中复制指定请求头到 Feign 请求模板。
     *
     * @param request         原始 HTTP 请求
     * @param requestTemplate Feign 请求模板
     * @param headerName      请求头名称
     */
    private void copyIfPresent(HttpServletRequest request, RequestTemplate requestTemplate, String headerName) {
        if (request == null) {
            return;
        }
        String value = request.getHeader(headerName);
        addHeaderIfPresent(requestTemplate, headerName, value);
    }

    /**
     * 值非空时向 Feign 请求模板添加请求头。
     *
     * @param requestTemplate Feign 请求模板
     * @param name            请求头名称
     * @param value           请求头值
     */
    private void addHeaderIfPresent(RequestTemplate requestTemplate, String name, String value) {
        if (value != null && !value.isBlank()) {
            requestTemplate.header(name, value);
        }
    }

    /**
     * 获取当前线程绑定的 HttpServletRequest（异步场景可能为 null）。
     *
     * @return 当前请求，或 null
     */
    private HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
}
