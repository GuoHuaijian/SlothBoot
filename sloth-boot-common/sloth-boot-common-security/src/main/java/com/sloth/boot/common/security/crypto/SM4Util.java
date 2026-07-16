package com.sloth.boot.common.security.crypto;

import com.sloth.boot.common.exception.SystemException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

/**
 * SM4 国密对称加解密工具类。
 * <p>
 * 基于 SM4/CBC/PKCS7Padding 算法，使用 BouncyCastle 作为加密提供者。 密钥长度为 16 字节（128 位），IV 长度为
 * 16 字节。 加密结果使用 Base64 编码输出。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class SM4Util {

    private SM4Util() {
        throw new UnsupportedOperationException("Utility class");
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "SM4/CBC/PKCS7Padding";

    /**
     * SM4 加密。
     *
     * @param data 待加密数据
     * @param key  密钥（16 字节）
     * @param iv   偏移量（16 字节）
     * @return Base64 编码的加密数据
     * @throws RuntimeException 加密失败时抛出
     */
    public static String encrypt(String data, String key, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "SM4");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw SystemException.of("SM4 encryption failed", e);
        }
    }

    /**
     * SM4 解密。
     *
     * @param data Base64 编码的加密数据
     * @param key  密钥（16 字节）
     * @param iv   偏移量（16 字节）
     * @return 解密后的原始数据
     * @throws RuntimeException 解密失败时抛出
     */
    public static String decrypt(String data, String key, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "SM4");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw SystemException.of("SM4 decryption failed", e);
        }
    }
}
