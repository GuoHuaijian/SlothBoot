package com.sloth.boot.common.security.crypto;

import com.sloth.boot.common.exception.SystemException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES 对称加解密工具类。
 * <p>
 * 基于 AES/CBC/PKCS5Padding 算法，密钥长度为 16 字节（128 位），IV 长度为 16 字节。 加密结果使用 Base64
 * 编码输出，解密时输入也应为 Base64 编码字符串。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class AESUtil {

    private AESUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * AES 加密
     *
     * @param data 待加密数据
     * @param key  密钥（16 字节）
     * @param iv   偏移量（16 字节）
     * @return Base64 编码的加密数据
     * @throws RuntimeException 加密失败时抛出
     */
    public static String encrypt(String data, String key, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw SystemException.of("AES encryption failed", e);
        }
    }

    /**
     * AES 解密。
     *
     * @param data Base64 编码的加密数据
     * @param key  密钥（16 字节）
     * @param iv   偏移量（16 字节）
     * @return 解密后的原始数据
     * @throws RuntimeException 解密失败时抛出
     */
    public static String decrypt(String data, String key, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw SystemException.of("AES decryption failed", e);
        }
    }
}
