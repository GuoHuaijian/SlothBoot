package com.sloth.boot.common.log.filter;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.log.config.LogProperties;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Trace 过滤器测试。
 */
@DisplayName("TraceFilter 测试")
class TraceFilterTest {

    private final TraceFilter traceFilter = new TraceFilter(new LogProperties());

    @AfterEach
    void cleanup() {
        TraceContext.clear();
        MDC.clear();
    }

    @Test
    @DisplayName("正常请求生成 traceId，写入 MDC 与响应头，并清理上下文")
    void generatesTraceIdAndCleansUp() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api");
        MockHttpServletResponse response = new MockHttpServletResponse();

        traceFilter.doFilter(request, response, (req, res) -> {
            assertThat(TraceContext.getTraceId()).isNotBlank();
            assertThat(MDC.get(HeaderConstant.MDC_TRACE_ID)).isEqualTo(TraceContext.getTraceId());
        });

        assertThat(response.getHeader(HeaderConstant.TRACE_ID)).isNotBlank();
        assertThat(TraceContext.getTraceId()).isNull();
        assertThat(MDC.get(HeaderConstant.MDC_TRACE_ID)).isNull();
    }

    @Test
    @DisplayName("非法入站 X-Trace-Id 不信任，重新生成合法 traceId")
    void invalidInboundTraceIdRegenerated() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api");
        request.addHeader(HeaderConstant.TRACE_ID, "bad\nheader");
        MockHttpServletResponse response = new MockHttpServletResponse();

        traceFilter.doFilter(request, response, (req, res) -> {
        });

        String responseTraceId = response.getHeader(HeaderConstant.TRACE_ID);
        assertThat(responseTraceId).isNotBlank().doesNotContain("\n").matches("[0-9a-zA-Z-]{1,64}");
    }

    @Test
    @DisplayName("合法入站 X-Trace-Id 被采纳并回显")
    void validInboundTraceIdAccepted() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api");
        request.addHeader(HeaderConstant.TRACE_ID, "abc-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        traceFilter.doFilter(request, response, (req, res) -> {
        });

        assertThat(response.getHeader(HeaderConstant.TRACE_ID)).isEqualTo("abc-123");
    }

    @Test
    @DisplayName("错误派发路径恢复 TraceContext，结束后清理")
    void errorDispatchRestoresTraceContext() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api");
        request.setAttribute(HeaderConstant.TRACE_ID, "existing-trace");
        AtomicReference<String> capturedInChain = new AtomicReference<>();

        traceFilter.doFilter(request, new MockHttpServletResponse(), (req, res) ->
            capturedInChain.set(TraceContext.getTraceId()));

        assertThat(capturedInChain.get()).isEqualTo("existing-trace");
        assertThat(TraceContext.getTraceId()).isNull();
        assertThat(MDC.get(HeaderConstant.MDC_TRACE_ID)).isNull();
    }

    @Test
    @DisplayName("排除路径不参与链路处理")
    void excludedUrlSkipped() throws Exception {
        LogProperties excludedProperties = new LogProperties();
        excludedProperties.getExcludeUrls().add("/health/**");
        TraceFilter excludedFilter = new TraceFilter(excludedProperties);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/health/check");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<Boolean> chainCalled = new AtomicReference<>(false);

        excludedFilter.doFilter(request, response, (req, res) -> chainCalled.set(true));

        assertThat(chainCalled.get()).isTrue();
        assertThat(TraceContext.getTraceId()).isNull();
    }
}
