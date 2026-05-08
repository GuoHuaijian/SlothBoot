package com.sloth.boot.common.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * 安全/加密工具类
 * <p>
 * 提供基础的摘要、签名、密码哈希等安全操作。底层使用 JDK 自带的加密 API，不引入第三方依赖。
 * <p>
 * 使用示例：
 * <pre>
 * // MD5 / SHA-256
 * String md5 = SecurityUtil.md5("hello");
 * String sha = SecurityUtil.sha256("hello");
 *
 * // 密码加盐哈希
 * String salt = SecurityUtil.generateSalt();
 * String hashed = SecurityUtil.hashPassword("myPassword", salt);
 * boolean match = SecurityUtil.verifyPassword("myPassword", salt, hashed);
 *
 * // 随机字符串
 * String token = SecurityUtil.generateRandomString(32);
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class SecurityUtil {

    private SecurityUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SALT_LENGTH = 16;

    /**
     * MD5 摘要
     *
     * @param input 输入字符串
     * @return 32 位小写十六进制 MD5
     */
    public static String md5(String input) {
        return digest("MD5", input);
    }

    /**
     * SHA-256 摘要
     *
     * @param input 输入字符串
     * @return 64 位小写十六进制 SHA-256
     */
    public static String sha256(String input) {
        return digest("SHA-256", input);
    }

    /**
     * HMAC-SHA256 签名
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
            return HexFormat.of().formatHex(result);
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 签名失败", e);
        }
    }

    /**
     * 生成随机盐（Base64 编码的 16 字节随机数据）
     *
     * @return 随机盐字符串
     */
    public static String generateSalt() {
        StringBuilder sb = new StringBuilder(SALT_LENGTH);
        for (int i = 0; i < SALT_LENGTH; i++) {
            sb.append(SALT_CHARS.charAt(SECURE_RANDOM.nextInt(SALT_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 密码加盐哈希（SHA-256）
     *
     * @param password 密码
     * @param salt     盐
     * @return 哈希后的密码
     */
    public static String hashPassword(String password, String salt) {
        return sha256(salt + password);
    }

    /**
     * 验证密码
     *
     * @param password       待验证密码
     * @param salt           盐
     * @param hashedPassword 已哈希的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        return hashPassword(password, salt).equals(hashedPassword);
    }

    /**
     * 生成指定长度的随机字符串（字母和数字）
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
            sb.append(SALT_CHARS.charAt(SECURE_RANDOM.nextInt(SALT_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 通用脱敏
     *
     * @param str       原始字符串
     * @param prefixLen 前缀保留长度
     * @param suffixLen 后缀保留长度
     * @return 脱敏后的字符串
     */
    public static String mask(String str, int prefixLen, int suffixLen) {
        return DesensitizeUtil.custom(str, prefixLen, suffixLen);
    }

    private static String digest(String algorithm, String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(algorithm + " 摘要失败", e);
        }
    }
}
