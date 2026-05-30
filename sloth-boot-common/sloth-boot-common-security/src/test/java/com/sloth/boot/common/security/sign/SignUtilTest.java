package com.sloth.boot.common.security.sign;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 签名工具测试。
 */
class SignUtilTest {

    private final String secretKey = "test-secret-key";
    private final String nonce = "abc123";

    @Test
    void should_generate_and_verify_sign() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "test");
        params.put("value", "123");

        long timestamp = System.currentTimeMillis();
        String sign = SignUtil.generateSign(params, secretKey, timestamp, nonce);

        assertThat(sign).isNotEmpty();
        assertThat(SignUtil.verifySign(params, sign, secretKey, timestamp, nonce)).isTrue();
    }

    @Test
    void should_reject_tampered_params() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "test");

        long timestamp = System.currentTimeMillis();
        String sign = SignUtil.generateSign(params, secretKey, timestamp, nonce);

        params.put("name", "tampered");
        assertThat(SignUtil.verifySign(params, sign, secretKey, timestamp, nonce)).isFalse();
    }

    @Test
    void should_pass_when_valid_time_is_zero() {
        Map<String, Object> params = Map.of("key", "value");
        long timestamp = System.currentTimeMillis() - 999999L;
        String sign = SignUtil.generateSign(params, secretKey, timestamp, nonce);

        assertThat(SignUtil.verifySign(params, sign, secretKey, timestamp, nonce, 0)).isTrue();
    }

    @Test
    void should_reject_expired_timestamp() {
        Map<String, Object> params = Map.of("key", "value");
        long timestamp = System.currentTimeMillis() - 600_000L;
        String sign = SignUtil.generateSign(params, secretKey, timestamp, nonce);

        assertThat(SignUtil.verifySign(params, sign, secretKey, timestamp, nonce, 300)).isFalse();
    }

    @Test
    void should_accept_fresh_timestamp() {
        Map<String, Object> params = Map.of("key", "value");
        long timestamp = System.currentTimeMillis();
        String sign = SignUtil.generateSign(params, secretKey, timestamp, nonce);

        assertThat(SignUtil.verifySign(params, sign, secretKey, timestamp, nonce, 300)).isTrue();
    }

    @Test
    void should_verify_sign_from_json() {
        String json = "{\"name\":\"test\",\"value\":\"123\"}";
        long timestamp = System.currentTimeMillis();
        String sign = SignUtil.generateSignFromJson(json, secretKey, timestamp, nonce);

        assertThat(SignUtil.verifySignFromJson(json, sign, secretKey, timestamp, nonce)).isTrue();
    }
}
