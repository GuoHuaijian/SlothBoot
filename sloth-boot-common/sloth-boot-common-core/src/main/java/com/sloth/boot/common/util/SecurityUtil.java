package com.sloth.boot.common.util;

import com.sloth.boot.common.exception.SystemException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * 安全工具类。
 * <p>
 * 提供 HMAC 签名和随机字符串生成。
 * <p>
 * 对于 MD5、SHA-256 摘要和 BCrypt 密码哈希，请使用 {@code com.sloth.boot.common.security.crypto.HashUtil}
 * （位于 sloth-boot-common-security 模块）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class SecurityUtil {

    private SecurityUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * HMAC-SHA256 签名。
     *
     * @param data 待签名数据
     * @param key  密钥
     * @return 十六进制签名字符串
     */
    public static String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(result);
        } catch (Exception e) {
            throw SystemException.of("HMAC-SHA256 signing failed", e);
        }
    }

    /**
     * 生成指定长度的随机字符串（字母和数字）。
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(SECURE_RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
