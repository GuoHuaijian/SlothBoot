package com.sloth.boot.common.security.sign;

import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.common.util.SecurityUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

/**
 * 请求签名工具类。
 * <p>
 * 提供基于 HMAC-SHA256 的请求签名机制。所有请求参数按字典序排列，key 与 value
 * 均做 URL 编码后拼接时间戳与随机数，再使用密钥进行 HMAC-SHA256 运算生成签名，
 * 用于防止请求篡改。
 * <p>
 * 编码保证待签串与参数一一对应：value 中的 {@code &}、{@code =} 等字符会被转义，
 * 无法通过重组参数结构伪造既有签名。
 * <p>
 * 防重放说明：时间戳窗口校验只能拒绝超出窗口的过期请求；nonce 的唯一性校验需要
 * 由调用方结合存储（如 Redis SETNX）实现，本类不负责 nonce 的持久化与查重。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class SignUtil {

    private SignUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 参数分隔符
     */
    private static final String PARAM_SEPARATOR = "&";

    /**
     * 键值分隔符
     */
    private static final String KEY_VALUE_SEPARATOR = "=";

    /**
     * 时间戳签名字段名
     */
    private static final String TIMESTAMP_FIELD = "timestamp";

    /**
     * 随机数签名字段名
     */
    private static final String NONCE_FIELD = "nonce";

    /**
     * 生成签名
     *
     * @param params    参数Map
     * @param secretKey 密钥
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 签名
     */
    public static String generateSign(Map<String, Object> params, String secretKey, long timestamp, String nonce) {
        return SecurityUtil.hmacSha256(buildSignContent(params, timestamp, nonce), secretKey);
    }

    /**
     * 验证签名（不含时间戳校验）
     *
     * @param params    参数Map
     * @param sign      签名
     * @param secretKey 密钥
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 是否验证成功
     */
    public static boolean verifySign(Map<String, Object> params, String sign, String secretKey, long timestamp,
                                     String nonce) {
        return verifySign(params, sign, secretKey, timestamp, nonce, 0);
    }

    /**
     * 验证签名并校验时间戳新鲜度
     *
     * @param params           参数Map
     * @param sign             签名
     * @param secretKey        密钥
     * @param timestamp        请求时间戳（毫秒）
     * @param nonce            随机数
     * @param validTimeSeconds 有效时间窗口（秒），0 或负数表示不校验
     * @return 是否验证成功
     */
    public static boolean verifySign(Map<String, Object> params, String sign, String secretKey, long timestamp,
                                     String nonce, int validTimeSeconds) {
        if (validTimeSeconds > 0) {
            long now = System.currentTimeMillis();
            if (Math.abs(now - timestamp) > validTimeSeconds * 1000L) {
                return false;
            }
        }
        String generatedSign = generateSign(params, secretKey, timestamp, nonce);
        if (sign == null) {
            return false;
        }
        return MessageDigest.isEqual(generatedSign.getBytes(StandardCharsets.UTF_8),
            sign.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 从 JSON 字符串生成签名
     *
     * @param json      JSON 字符串
     * @param secretKey 密钥
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 签名
     */
    public static String generateSignFromJson(String json, String secretKey, long timestamp, String nonce) {
        Map<String, Object> params = JsonUtil.parseObject(json, Map.class);
        return generateSign(params, secretKey, timestamp, nonce);
    }

    /**
     * 从 JSON 字符串验证签名
     *
     * @param json      JSON 字符串
     * @param sign      签名
     * @param secretKey 密钥
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 是否验证成功
     */
    public static boolean verifySignFromJson(String json, String sign, String secretKey, long timestamp, String nonce) {
        return verifySignFromJson(json, sign, secretKey, timestamp, nonce, 0);
    }

    /**
     * 从 JSON 字符串验证签名并校验时间戳新鲜度
     *
     * @param json             JSON 字符串
     * @param sign             签名
     * @param secretKey        密钥
     * @param timestamp        请求时间戳（毫秒）
     * @param nonce            随机数
     * @param validTimeSeconds 有效时间窗口（秒），0 或负数表示不校验
     * @return 是否验证成功
     */
    public static boolean verifySignFromJson(String json, String sign, String secretKey, long timestamp,
                                             String nonce, int validTimeSeconds) {
        Map<String, Object> params = JsonUtil.parseObject(json, Map.class);
        return verifySign(params, sign, secretKey, timestamp, nonce, validTimeSeconds);
    }

    /**
     * 构建待签名字符串。
     * <p>
     * 参数按 key 字典序排列，key 与 value 均经 URL 编码后以 {@code k=v} 形式拼接，
     * 最后追加时间戳与随机数字段。
     *
     * @param params    参数Map
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 待签名字符串
     */
    private static String buildSignContent(Map<String, Object> params, long timestamp, String nonce) {
        TreeMap<String, Object> sortedParams = new TreeMap<>(params == null ? Map.of() : params);
        StringBuilder sb = new StringBuilder(64);
        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append(PARAM_SEPARATOR);
            }
            sb.append(percentEncode(entry.getKey()))
                .append(KEY_VALUE_SEPARATOR)
                .append(percentEncode(asString(entry.getValue())));
        }
        sb.append(PARAM_SEPARATOR).append(TIMESTAMP_FIELD).append(KEY_VALUE_SEPARATOR).append(timestamp)
            .append(PARAM_SEPARATOR).append(NONCE_FIELD).append(KEY_VALUE_SEPARATOR).append(percentEncode(nonce));
        return sb.toString();
    }

    /**
     * URL 编码（空格编码为 %20，符合 RFC 3986）。
     *
     * @param value 原始字符串
     * @return 编码后的字符串
     */
    private static String percentEncode(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    /**
     * 参数值转字符串，null 视为空串。
     *
     * @param value 参数值
     * @return 字符串形式
     */
    private static String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
