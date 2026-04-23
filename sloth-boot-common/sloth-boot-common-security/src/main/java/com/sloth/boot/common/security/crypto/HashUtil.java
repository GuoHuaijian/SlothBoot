package com.sloth.boot.common.security.crypto;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 哈希工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class HashUtil {

    /**
     * MD5 哈希
     *
     * @param data 待哈希数据
     * @return 哈希后的数据
     */
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 哈希失败", e);
        }
    }

    /**
     * SHA-256 哈希
     *
     * @param data 待哈希数据
     * @return 哈希后的数据
     */
    public static String sha256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 哈希失败", e);
        }
    }

    /**
     * BCrypt 哈希
     *
     * @param data 待哈希数据
     * @return 哈希后的数据
     */
    public static String bcrypt(String data) {
        return BCrypt.hashpw(data, BCrypt.gensalt());
    }

    /**
     * BCrypt 验证
     *
     * @param data       原始数据
     * @param hashedData 哈希数据
     * @return 是否验证成功
     */
    public static boolean verifyBcrypt(String data, String hashedData) {
        return BCrypt.checkpw(data, hashedData);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}