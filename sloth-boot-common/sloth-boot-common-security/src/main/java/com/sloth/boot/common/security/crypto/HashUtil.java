package com.sloth.boot.common.security.crypto;

import com.sloth.boot.common.exception.SystemException;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * 哈希摘要工具类。
 * <p>
 * 提供 MD5、SHA-256 单向哈希以及 BCrypt 密码哈希功能。 BCrypt 内部自带盐值生成，适合存储用户密码等场景。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class HashUtil {

    private HashUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 计算 MD5 摘要。
     *
     * @param data 待哈希数据
     * @return 32 位十六进制哈希字符串
     * @throws RuntimeException 算法不可用时抛出
     */
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw SystemException.of("MD5 hash failed", e);
        }
    }

    /**
     * 计算 SHA-256 摘要。
     *
     * @param data 待哈希数据
     * @return 64 位十六进制哈希字符串
     * @throws RuntimeException 算法不可用时抛出
     */
    public static String sha256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw SystemException.of("SHA-256 hash failed", e);
        }
    }

    /**
     * 使用 BCrypt 算法进行密码哈希（内部自动生成盐值）。
     *
     * @param data 待哈希数据（如用户密码）
     * @return BCrypt 哈希字符串（含盐值）
     */
    public static String bcrypt(String data) {
        return BCrypt.hashpw(data, BCrypt.gensalt());
    }

    /**
     * 验证原始数据是否与 BCrypt 哈希值匹配。
     *
     * @param data       原始数据（如用户输入的密码）
     * @param hashedData BCrypt 哈希数据
     * @return {@code true} 验证成功，{@code false} 验证失败
     */
    public static boolean verifyBcrypt(String data, String hashedData) {
        return BCrypt.checkpw(data, hashedData);
    }

    /**
     * 将字节数组转换为十六进制字符串。
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }
}
