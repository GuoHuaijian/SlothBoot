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

    @Test
    void should_reject_parameter_smuggling_with_same_canonical_form() {
        long timestamp = System.currentTimeMillis();
        Map<String, Object> signed = Map.of("x", "1", "y", "2");
        String sign = SignUtil.generateSign(signed, secretKey, timestamp, nonce);

        // 重组参数结构后待签串不同，旧签名必须失效
        Map<String, Object> smuggled = Map.of("x", "1&y=2");
        assertThat(SignUtil.verifySign(smuggled, sign, secretKey, timestamp, nonce)).isFalse();
    }

    @Test
    void should_roundtrip_values_containing_special_chars() {
        Map<String, Object> params = new HashMap<>();
        params.put("redirect", "https://a.com/?x=1&y=2");
        params.put("note", "a=b c&中文%100");

        long timestamp = System.currentTimeMillis();
        String sign = SignUtil.generateSign(params, secretKey, timestamp, nonce);

        assertThat(SignUtil.verifySign(params, sign, secretKey, timestamp, nonce)).isTrue();
    }

    @Test
    void should_reject_null_sign() {
        Map<String, Object> params = Map.of("key", "value");

        assertThat(SignUtil.verifySign(params, null, secretKey, System.currentTimeMillis(), nonce)).isFalse();
    }
}
