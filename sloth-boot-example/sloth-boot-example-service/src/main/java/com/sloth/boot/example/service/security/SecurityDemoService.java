package com.sloth.boot.example.service.security;

import com.sloth.boot.common.security.crypto.AESUtil;
import com.sloth.boot.common.security.crypto.HashUtil;
import com.sloth.boot.common.security.crypto.RSAUtil;
import com.sloth.boot.common.security.sign.SignUtil;
import com.sloth.boot.common.security.xss.XssCleaner;
import com.sloth.boot.example.model.security.vo.CryptoResponse;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Map;
import java.util.UUID;

/**
 * 安全演示服务 - 展示 AES/RSA 加解密、BCrypt 哈希、请求签名等能力
 * <p>
 * 无状态纯工具委托，不持有任何实例变量
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Service
public class SecurityDemoService {

    // DEMO ONLY — 生产环境请通过 application.yml 配置
    private static final String DEFAULT_AES_KEY = "slothboot12345678";
    // DEMO ONLY — 生产环境请通过 application.yml 配置
    private static final String DEFAULT_AES_IV = "slothboot12345678";
    // DEMO ONLY — 生产环境请通过 application.yml 配置
    private static final String DEFAULT_SECRET_KEY = "demo-secret-key";

    // ==================== AES 对称加密 ====================

    /**
     * AES 加密（key/iv 为 null 时使用默认值）
     *
     * @param data 待加密数据
     * @param key  加密密钥（可选）
     * @param iv   初始化向量（可选）
     * @return 加密结果
     */
    public CryptoResponse aesEncrypt(String data, String key, String iv) {
        String actualKey = key != null ? key : DEFAULT_AES_KEY;
        String actualIv = iv != null ? iv : DEFAULT_AES_IV;
        String encrypted = AESUtil.encrypt(data, actualKey, actualIv);
        return CryptoResponse.builder().result(encrypted).build();
    }

    /**
     * AES 解密（key/iv 为 null 时使用默认值）
     *
     * @param data 待解密数据
     * @param key  解密密钥（可选）
     * @param iv   初始化向量（可选）
     * @return 解密结果
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
     *
     * @return 包含公钥和私钥的响应
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
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return 加密结果
     */
    public CryptoResponse rsaEncrypt(String data, String publicKey) {
        String encrypted = RSAUtil.encrypt(data, publicKey);
        return CryptoResponse.builder().result(encrypted).build();
    }

    /**
     * RSA 私钥解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return 解密结果
     */
    public CryptoResponse rsaDecrypt(String data, String privateKey) {
        String decrypted = RSAUtil.decrypt(data, privateKey);
        return CryptoResponse.builder().result(decrypted).build();
    }

    /**
     * RSA 私钥签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return 签名结果
     */
    public CryptoResponse rsaSign(String data, String privateKey) {
        String sign = RSAUtil.sign(data, privateKey);
        return CryptoResponse.builder().result(sign).build();
    }

    /**
     * RSA 公钥验签
     *
     * @param data      原始数据
     * @param sign      签名值
     * @param publicKey 公钥
     * @return 验签结果
     */
    public CryptoResponse rsaVerify(String data, String sign, String publicKey) {
        boolean verified = RSAUtil.verify(data, sign, publicKey);
        return CryptoResponse.builder().verified(verified).build();
    }

    // ==================== 哈希摘要 ====================

    /**
     * BCrypt 密码哈希（附带耗时统计）
     *
     * @param password 明文密码
     * @return 哈希结果及耗时
     */
    public CryptoResponse bcryptHash(String password) {
        long start = System.currentTimeMillis();
        String hash = HashUtil.bcrypt(password);
        long costMs = System.currentTimeMillis() - start;
        return CryptoResponse.builder().result(hash).costMs(costMs).build();
    }

    /**
     * BCrypt 密码验证（附带耗时统计）
     *
     * @param password 明文密码
     * @param hash     哈希值
     * @return 验证结果及耗时
     */
    public CryptoResponse bcryptVerify(String password, String hash) {
        long start = System.currentTimeMillis();
        boolean verified = HashUtil.verifyBcrypt(password, hash);
        long costMs = System.currentTimeMillis() - start;
        return CryptoResponse.builder().verified(verified).costMs(costMs).build();
    }

    /**
     * SHA-256 摘要
     *
     * @param data 待摘要数据
     * @return 摘要结果
     */
    public CryptoResponse sha256(String data) {
        String hash = HashUtil.sha256(data);
        return CryptoResponse.builder().result(hash).build();
    }

    // ==================== 请求签名 ====================

    /**
     * 生成请求签名（自动生成 timestamp/nonce）
     *
     * @param params    请求参数
     * @param secretKey 密钥（可选）
     * @return 签名结果（含 timestamp/nonce）
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
     *
     * @param params    请求参数
     * @param sign      签名值
     * @param secretKey 密钥（可选）
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 验签结果
     */
    public CryptoResponse verifySign(Map<String, Object> params, String sign,
                                     String secretKey, long timestamp, String nonce) {
        String actualKey = secretKey != null ? secretKey : DEFAULT_SECRET_KEY;
        boolean verified = SignUtil.verifySign(params, sign, actualKey, timestamp, nonce);
        return CryptoResponse.builder().verified(verified).build();
    }

    // ==================== XSS 清洗 ====================

    /**
     * XSS 内容清洗
     *
     * @param content 待清洗内容
     * @return 清洗结果（含原始内容与处理后内容）
     */
    public CryptoResponse cleanXss(String content) {
        String cleaned = XssCleaner.cleanText(content);
        return CryptoResponse.builder()
                .original(content)
                .processed(cleaned)
                .build();
    }
}
