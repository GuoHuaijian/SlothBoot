package com.sloth.boot.starter.web.filter;

import com.sloth.boot.common.security.sign.SignProperties;
import com.sloth.boot.common.security.sign.SignUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 签名过滤器测试。
 */
@DisplayName("SignFilter 测试")
class SignFilterTest {

    private static final String SECRET = "test-secret";

    private SignProperties signProperties(boolean enabled) {
        SignProperties properties = new SignProperties();
        properties.setEnabled(enabled);
        properties.setSecretKey(SECRET);
        return properties;
    }

    @Test
    @DisplayName("合法签名放行")
    void validSignaturePasses() throws Exception {
        SignFilter filter = new SignFilter(signProperties(true));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/query");
        request.addParameter("name", "sloth");
        long timestamp = System.currentTimeMillis();
        String nonce = "nonce-1";
        String sign = SignUtil.generateSign(Map.of("name", "sloth"), SECRET, timestamp, nonce);
        request.addHeader(SignFilter.SIGN_HEADER, sign);
        request.addHeader(SignFilter.TIMESTAMP_HEADER, String.valueOf(timestamp));
        request.addHeader(SignFilter.NONCE_HEADER, nonce);
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        filter.doFilter(request, response, (req, res) -> chainCalled.set(true));

        assertThat(chainCalled.get()).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("缺少签名头返回 401")
    void missingSignHeaderRejected() throws Exception {
        SignFilter filter = new SignFilter(signProperties(true));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/query");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {
        });

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("错误签名返回 401")
    void invalidSignatureRejected() throws Exception {
        SignFilter filter = new SignFilter(signProperties(true));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/query");
        request.addParameter("name", "sloth");
        long timestamp = System.currentTimeMillis();
        String nonce = "nonce-2";
        request.addHeader(SignFilter.SIGN_HEADER, "deadbeef");
        request.addHeader(SignFilter.TIMESTAMP_HEADER, String.valueOf(timestamp));
        request.addHeader(SignFilter.NONCE_HEADER, nonce);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {
        });

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("同一 nonce 重放被拒绝")
    void replayedNonceRejected() throws Exception {
        SignFilter filter = new SignFilter(signProperties(true));
        long timestamp = System.currentTimeMillis();
        String nonce = "nonce-3";
        String sign = SignUtil.generateSign(Map.of(), SECRET, timestamp, nonce);

        MockHttpServletRequest first = requestWith(sign, timestamp, nonce);
        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        filter.doFilter(first, firstResponse, (req, res) -> {
        });
        assertThat(firstResponse.getStatus()).isEqualTo(200);

        MockHttpServletRequest second = requestWith(sign, timestamp, nonce);
        MockHttpServletResponse secondResponse = new MockHttpServletResponse();
        filter.doFilter(second, secondResponse, (req, res) -> {
        });
        assertThat(secondResponse.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("排除路径跳过签名验证")
    void excludedPathSkipped() throws Exception {
        SignProperties properties = signProperties(true);
        properties.getExcludePaths().add("/health/**");
        SignFilter filter = new SignFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/health/check");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        filter.doFilter(request, response, (req, res) -> chainCalled.set(true));

        assertThat(chainCalled.get()).isTrue();
    }

    private MockHttpServletRequest requestWith(String sign, long timestamp, String nonce) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/query");
        request.addHeader(SignFilter.SIGN_HEADER, sign);
        request.addHeader(SignFilter.TIMESTAMP_HEADER, String.valueOf(timestamp));
        request.addHeader(SignFilter.NONCE_HEADER, nonce);
        return request;
    }
}
