package com.sloth.boot.example.service;

import com.sloth.boot.common.security.crypto.AESUtil;
import com.sloth.boot.common.security.crypto.HashUtil;
import com.sloth.boot.common.security.crypto.RSAUtil;
import com.sloth.boot.common.security.sign.SignUtil;
import com.sloth.boot.example.dto.CryptoResponse;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Map;
import java.util.UUID;

/**
 * 安全演示服务 - 展示 AES/RSA 加解密、BCrypt 哈希、请求签名等能力
 * <p>
 * 无状态纯工具委托，不持有任何实例变量
 */
@Service
public class SecurityDemoService {

    private static final String DEFAULT_AES_KEY = "slothboot12345678";
    private static final String DEFAULT_AES_IV = "slothboot12345678";
    private static final String DEFAULT_SECRET_KEY = "demo-secret-key";

    // ==================== AES 对称加密 ====================

    /**
     * AES 加密（key/iv 为 null 时使用默认值）
     */
    public CryptoResponse aesEncrypt(String data, String key, String iv) {
        String actualKey = key != null ? key : DEFAULT_AES_KEY;
        String actualIv = iv != null ? iv : DEFAULT_AES_IV;
        String encrypted = AESUtil.encrypt(data, actualKey, actualIv);
        return CryptoResponse.builder().result(encrypted).build();
    }

    /**
     * AES 解密（key/iv 为 null 时使用默认值）
     */
    public CryptoResponse aesDecrypt(String data, String key, String iv) {
        String actualKey = key != null ? key : DEFAULT_AES_KEY;
        String actualIv = iv != null ? iv : DEFAULT_AES_IV;
        String decrypted = AESUtil.decrypt(data, actualKey, actualIv);
        return CryptoResponse.builder().result(decrypted).build();
    }

    // ==================== RSA 非对称加密 ====================

    /**
     * 生成 RSA 密钥对
     */
    public CryptoResponse rsaGenerateKeypair() {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        return CryptoResponse.builder()
                .publicKey(RSAUtil.getPublicKey(keyPair))
                .privateKey(RSAUtil.getPrivateKey(keyPair))
                .build();
    }

    /**
     * RSA 公钥加密
     */
    public CryptoResponse rsaEncrypt(String data, String publicKey) {
        String encrypted = RSAUtil.encrypt(data, publicKey);
        return CryptoResponse.builder().result(encrypted).build();
    }

    /**
     * RSA 私钥解密
     */
    public CryptoResponse rsaDecrypt(String data, String privateKey) {
        String decrypted = RSAUtil.decrypt(data, privateKey);
        return CryptoResponse.builder().result(decrypted).build();
    }

    /**
     * RSA 私钥签名
     */
    public CryptoResponse rsaSign(String data, String privateKey) {
        String sign = RSAUtil.sign(data, privateKey);
        return CryptoResponse.builder().result(sign).build();
    }

    /**
     * RSA 公钥验签
     */
    public CryptoResponse rsaVerify(String data, String sign, String publicKey) {
        boolean verified = RSAUtil.verify(data, sign, publicKey);
        return CryptoResponse.builder().verified(verified).build();
    }

    // ==================== 哈希摘要 ====================

    /**
     * BCrypt 密码哈希（附带耗时统计）
     */
    public CryptoResponse bcryptHash(String password) {
        long start = System.currentTimeMillis();
        String hash = HashUtil.bcrypt(password);
        long costMs = System.currentTimeMillis() - start;
        return CryptoResponse.builder().result(hash).costMs(costMs).build();
    }

    /**
     * BCrypt 密码验证（附带耗时统计）
     */
    public CryptoResponse bcryptVerify(String password, String hash) {
        long start = System.currentTimeMillis();
        boolean verified = HashUtil.verifyBcrypt(password, hash);
        long costMs = System.currentTimeMillis() - start;
        return CryptoResponse.builder().verified(verified).costMs(costMs).build();
    }

    /**
     * SHA-256 摘要
     */
    public CryptoResponse sha256(String data) {
        String hash = HashUtil.sha256(data);
        return CryptoResponse.builder().result(hash).build();
    }

    // ==================== 请求签名 ====================

    /**
     * 生成请求签名（自动生成 timestamp/nonce）
     */
    public CryptoResponse generateSign(Map<String, Object> params, String secretKey) {
        String actualKey = secretKey != null ? secretKey : DEFAULT_SECRET_KEY;
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().replace("-", "");
        String sign = SignUtil.generateSign(params, actualKey, timestamp, nonce);
        return CryptoResponse.builder()
                .sign(sign)
                .timestamp(timestamp)
                .nonce(nonce)
                .build();
    }

    /**
     * 验证请求签名（需要提供 timestamp/nonce）
     */
    public CryptoResponse verifySign(Map<String, Object> params, String sign,
                                     String secretKey, long timestamp, String nonce) {
        String actualKey = secretKey != null ? secretKey : DEFAULT_SECRET_KEY;
        boolean verified = SignUtil.verifySign(params, sign, actualKey, timestamp, nonce);
        return CryptoResponse.builder().verified(verified).build();
    }

    // ==================== XSS 清洗 ====================

    /**
     * XSS 内容清洗（简单正则方式，演示用）
     */
    public CryptoResponse cleanXss(String content) {
        String cleaned = content
                .replaceAll("<script[^>]*>.*?</script>", "")
                .replaceAll("on\\w+\\s*=", "");
        return CryptoResponse.builder()
                .original(content)
                .processed(cleaned)
                .build();
    }
}
