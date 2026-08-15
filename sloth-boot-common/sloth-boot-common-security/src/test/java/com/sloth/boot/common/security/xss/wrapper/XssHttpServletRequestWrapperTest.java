package com.sloth.boot.common.security.xss.wrapper;

import com.sloth.boot.common.security.xss.XssProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * XSS 请求包装器测试。
 */
@DisplayName("XssHttpServletRequestWrapper 测试")
class XssHttpServletRequestWrapperTest {

    private final XssProperties xssProperties = new XssProperties();

    @Test
    @DisplayName("getParameterMap 中的值经过 XSS 清洗，无绕过路径")
    void cleansParameterMap() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "<script>alert(1)</script>");
        request.addParameter("safe", "hello");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request, xssProperties);
        Map<String, String[]> parameterMap = wrapper.getParameterMap();

        assertThat(parameterMap.get("name")[0]).isEmpty();
        assertThat(parameterMap.get("safe")[0]).isEqualTo("hello");
    }

    @Test
    @DisplayName("getParameter 单个参数经过 XSS 清洗")
    void cleansSingleParameter() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "<img src=x onerror=\"alert(1)\">");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request, xssProperties);
        assertThat(wrapper.getParameter("name")).doesNotContain("onerror");
    }

    @Test
    @DisplayName("getQueryString 经过 XSS 清洗")
    void cleansQueryString() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setQueryString("name=<script>alert(1)</script>");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request, xssProperties);
        assertThat(wrapper.getQueryString()).isEqualTo("name=");
    }

    @Test
    @DisplayName("请求体可重复读取")
    void bodyCanBeReadMultipleTimes() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("{\"name\":\"sloth\"}".getBytes());

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request, xssProperties);
        assertThat(new String(wrapper.getInputStream().readAllBytes())).isEqualTo("{\"name\":\"sloth\"}");
        assertThat(new String(wrapper.getInputStream().readAllBytes())).isEqualTo("{\"name\":\"sloth\"}");
    }
}
