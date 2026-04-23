package com.sloth.boot.common.security.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

/**
 * SM4 加解密工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class SM4Util {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "SM4/CBC/PKCS7Padding";

    /**
     * SM4 加密
     *
     * @param data      待加密数据
     * @param key       密钥
     * @param iv        偏移量
     * @return 加密后的数据
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
            throw new RuntimeException("SM4 加密失败", e);
        }
    }

    /**
     * SM4 解密
     *
     * @param data      待解密数据
     * @param key       密钥
     * @param iv        偏移量
     * @return 解密后的数据
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
            throw new RuntimeException("SM4 解密失败", e);
        }
    }
}