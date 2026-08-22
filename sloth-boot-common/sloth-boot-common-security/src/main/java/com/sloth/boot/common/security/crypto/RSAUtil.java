package com.sloth.boot.common.security.crypto;

import com.sloth.boot.common.exception.SystemException;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 非对称加解密与签名工具类。
 * <p>
 * 使用 RSA 算法（2048 位密钥），支持公钥加密/私钥解密以及数字签名（SHA256withRSA）。 密钥以 Base64
 * 编码字符串形式传入和输出，格式为 PKCS#8（私钥）/ X.509（公钥）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class RSAUtil {

    private RSAUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "RSA";

    /**
     * 加密变换：PKCS#1 v1.5 填充（旧版兼容，显式声明避免 Provider 默认值差异）
     */
    private static final String CIPHER_PKCS1 = "RSA/ECB/PKCS1Padding";

    /**
     * 加密变换：OAEPWithSHA-256（推荐，抗 Bleichenbacher 填充预言攻击）
     */
    private static final String CIPHER_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    /**
     * 生成 RSA 密钥对（2048 位）。
     *
     * @return RSA 密钥对
     * @throws RuntimeException 密钥对生成失败时抛出
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw SystemException.of("Generate key pair failed", e);
        }
    }

    /**
     * 从密钥对提取 Base64 编码的公钥字符串。
     *
     * @param keyPair 密钥对
     * @return Base64 编码的公钥字符串
     */
    public static String getPublicKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 从密钥对提取 Base64 编码的私钥字符串。
     *
     * @param keyPair 密钥对
     * @return Base64 编码的私钥字符串
     */
    public static String getPrivateKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    /**
     * 使用公钥进行 RSA-OAEP 加密（推荐）。
     * <p>
     * 使用 OAEPWithSHA-256 填充，与 {@link #decryptOaep(String, String)} 配对使用。
     *
     * @param data      待加密数据
     * @param publicKey Base64 编码的公钥
     * @return Base64 编码的加密数据
     * @throws RuntimeException 加密失败时抛出
     */
    public static String encryptOaep(String data, String publicKey) {
        return doEncrypt(data, publicKey, CIPHER_OAEP);
    }

    /**
     * 使用私钥进行 RSA-OAEP 解密（推荐）。
     *
     * @param data       Base64 编码的加密数据
     * @param privateKey Base64 编码的私钥
     * @return 解密后的原始数据
     * @throws RuntimeException 解密失败时抛出
     */
    public static String decryptOaep(String data, String privateKey) {
        return doDecrypt(data, privateKey, CIPHER_OAEP);
    }

    /**
     * 使用公钥进行 RSA 加密（PKCS#1 v1.5 填充，显式声明）。
     *
     * @param data      待加密数据
     * @param publicKey Base64 编码的公钥
     * @return Base64 编码的加密数据
     * @throws RuntimeException 加密失败时抛出
     * @deprecated PKCS#1 v1.5 填充存在 Bleichenbacher 填充预言风险；请改用 {@link #encryptOaep(String, String)}
     */
    @Deprecated(since = "1.0.0")
    public static String encrypt(String data, String publicKey) {
        return doEncrypt(data, publicKey, CIPHER_PKCS1);
    }

    /**
     * 使用私钥进行 RSA 解密（PKCS#1 v1.5 填充，显式声明）。
     *
     * @param data       Base64 编码的加密数据
     * @param privateKey Base64 编码的私钥
     * @return 解密后的原始数据
     * @throws RuntimeException 解密失败时抛出
     * @deprecated PKCS#1 v1.5 填充存在填充预言风险；请改用 {@link #decryptOaep(String, String)}
     */
    @Deprecated(since = "1.0.0")
    public static String decrypt(String data, String privateKey) {
        return doDecrypt(data, privateKey, CIPHER_PKCS1);
    }

    private static String doEncrypt(String data, String publicKey, String transformation) {
        try {
            PublicKey key = getPublicKeyFromString(publicKey);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw SystemException.of("RSA encryption failed", e);
        }
    }

    private static String doDecrypt(String data, String privateKey, String transformation) {
        try {
            PrivateKey key = getPrivateKeyFromString(privateKey);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw SystemException.of("RSA decryption failed", e);
        }
    }

    /**
     * 使用私钥对数据进行签名（SHA256withRSA）。
     *
     * @param data       待签名数据
     * @param privateKey Base64 编码的私钥
     * @return Base64 编码的签名
     * @throws RuntimeException 签名失败时抛出
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
            throw SystemException.of("RSA signing failed", e);
        }
    }

    /**
     * 使用公钥验证签名（SHA256withRSA）。
     * <p>
     * 签名值来自外部输入：非法 Base64 或格式错误的签名返回 {@code false} 而非抛出异常，
     * 密钥配置错误仍会快速失败。
     *
     * @param data      原始数据
     * @param sign      Base64 编码的签名
     * @param publicKey Base64 编码的公钥
     * @return {@code true} 验证成功，{@code false} 验证失败
     * @throws RuntimeException 公钥解析失败时抛出
     */
    public static boolean verify(String data, String sign, String publicKey) {
        PublicKey key = getPublicKey(publicKey);
        Signature signature = getSignature(SIGN_ALGORITHM);
        try {
            signature.initVerify(key);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (IllegalArgumentException | java.security.SignatureException e) {
            // 外部签名数据不合法，视为验签失败而非系统错误
            return false;
        } catch (Exception e) {
            throw SystemException.of("RSA verification failed", e);
        }
    }

    private static PublicKey getPublicKey(String publicKey) {
        try {
            return getPublicKeyFromString(publicKey);
        } catch (Exception e) {
            throw SystemException.of("RSA verification failed: invalid public key", e);
        }
    }

    private static Signature getSignature(String algorithm) {
        try {
            return Signature.getInstance(algorithm);
        } catch (Exception e) {
            throw SystemException.of("RSA signature algorithm unavailable: " + algorithm, e);
        }
    }

    /**
     * 从 Base64 字符串还原 X.509 格式公钥。
     *
     * @param publicKey Base64 编码的公钥
     * @return 公钥对象
     * @throws Exception 解析失败时抛出
     */
    private static PublicKey getPublicKeyFromString(String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 从 Base64 字符串还原 PKCS#8 格式私钥。
     *
     * @param privateKey Base64 编码的私钥
     * @return 私钥对象
     * @throws Exception 解析失败时抛出
     */
    private static PrivateKey getPrivateKeyFromString(String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }
}
