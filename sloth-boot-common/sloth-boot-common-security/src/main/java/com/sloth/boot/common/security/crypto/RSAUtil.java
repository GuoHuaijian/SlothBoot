package com.sloth.boot.common.security.crypto;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 加解密工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RSAUtil {

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    /**
     * 生成密钥对
     *
     * @return 密钥对
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("生成密钥对失败", e);
        }
    }

    /**
     * 获取公钥字符串
     *
     * @param keyPair 密钥对
     * @return 公钥字符串
     */
    public static String getPublicKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 获取私钥字符串
     *
     * @param keyPair 密钥对
     * @return 私钥字符串
     */
    public static String getPrivateKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    /**
     * RSA 加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return 加密后的数据
     */
    public static String encrypt(String data, String publicKey) {
        try {
            PublicKey key = getPublicKeyFromString(publicKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("RSA 加密失败", e);
        }
    }

    /**
     * RSA 解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return 解密后的数据
     */
    public static String decrypt(String data, String privateKey) {
        try {
            PrivateKey key = getPrivateKeyFromString(privateKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA 解密失败", e);
        }
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, String privateKey) {
        try {
            PrivateKey key = getPrivateKeyFromString(privateKey);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initSign(key);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            throw new RuntimeException("签名失败", e);
        }
    }

    /**
     * 验签
     *
     * @param data      原始数据
     * @param sign      签名
     * @param publicKey 公钥
     * @return 是否验证成功
     */
    public static boolean verify(String data, String sign, String publicKey) {
        try {
            PublicKey key = getPublicKeyFromString(publicKey);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(key);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            throw new RuntimeException("验签失败", e);
        }
    }

    /**
     * 从字符串获取公钥
     */
    private static PublicKey getPublicKeyFromString(String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 从字符串获取私钥
     */
    private static PrivateKey getPrivateKeyFromString(String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }
}