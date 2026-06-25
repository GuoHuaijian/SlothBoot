package com.sloth.boot.example.application.command.security;

import cn.hutool.crypto.digest.BCrypt;
import com.sloth.boot.common.security.crypto.AESUtil;
import com.sloth.boot.common.security.crypto.HashUtil;
import com.sloth.boot.common.security.crypto.RSAUtil;
import com.sloth.boot.common.security.sign.SignUtil;
import com.sloth.boot.common.security.xss.XssCleaner;
import com.sloth.boot.example.application.model.vo.security.CryptoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Map;

/**
 * 加解密演示服务。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityCommand {

    /**
     * AES加密。
     *
     * @param data 待加密数据
     * @param key  密钥
     * @param iv   初始化向量
     * @return 加密结果
     */
    public CryptoResponse aesEncrypt(String data, String key, String iv) {
        String encrypted = AESUtil.encrypt(data, key, iv);
        return CryptoResponse.builder().result(encrypted).original(data).processed(encrypted).build();
    }

    /**
     * AES解密。
     *
     * @param data 待解密数据
     * @param key  密钥
     * @param iv   初始化向量
     * @return 解密结果
     */
    public CryptoResponse aesDecrypt(String data, String key, String iv) {
        String decrypted = AESUtil.decrypt(data, key, iv);
        return CryptoResponse.builder().result(decrypted).original(data).processed(decrypted).build();
    }

    /**
     * 生成RSA密钥对。
     *
     * @return 包含公钥和私钥的密钥对
     */
    public CryptoResponse generateRsaKeyPair() {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        String publicKey = RSAUtil.getPublicKey(keyPair);
        String privateKey = RSAUtil.getPrivateKey(keyPair);
        return CryptoResponse.builder().publicKey(publicKey).privateKey(privateKey).build();
    }

    /**
     * RSA加密。
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return 加密结果
     */
    public CryptoResponse rsaEncrypt(String data, String publicKey) {
        String encrypted = RSAUtil.encrypt(data, publicKey);
        return CryptoResponse.builder().result(encrypted).original(data).processed(encrypted).build();
    }

    /**
     * RSA解密。
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return 解密结果
     */
    public CryptoResponse rsaDecrypt(String data, String privateKey) {
        String decrypted = RSAUtil.decrypt(data, privateKey);
        return CryptoResponse.builder().result(decrypted).original(data).processed(decrypted).build();
    }

    /**
     * RSA签名。
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return 签名结果
     */
    public CryptoResponse rsaSign(String data, String privateKey) {
        String sign = RSAUtil.sign(data, privateKey);
        return CryptoResponse.builder().sign(sign).original(data).build();
    }

    /**
     * RSA验签。
     *
     * @param data      原始数据
     * @param sign      签名
     * @param publicKey 公钥
     * @return 验证结果
     */
    public CryptoResponse rsaVerify(String data, String sign, String publicKey) {
        boolean verified = RSAUtil.verify(data, sign, publicKey);
        return CryptoResponse.builder().verified(verified).original(data).sign(sign).build();
    }

    /**
     * BCrypt哈希。
     *
     * @param data 待哈希数据
     * @return 哈希结果
     */
    public CryptoResponse bcryptHash(String data) {
        String hash = BCrypt.hashpw(data, BCrypt.gensalt());
        return CryptoResponse.builder().result(hash).original(data).build();
    }

    /**
     * BCrypt验证。
     *
     * @param data 原始数据
     * @param hash 哈希值
     * @return 验证结果
     */
    public CryptoResponse bcryptVerify(String data, String hash) {
        boolean verified = HashUtil.verifyBcrypt(data, hash);
        return CryptoResponse.builder().verified(verified).original(data).processed(hash).build();
    }

    /**
     * SHA-256哈希。
     *
     * @param data 待哈希数据
     * @return 哈希结果
     */
    public CryptoResponse sha256Hash(String data) {
        String hash = HashUtil.sha256(data);
        return CryptoResponse.builder().result(hash).original(data).processed(hash).build();
    }

    /**
     * 生成签名。
     *
     * @param params    参数集合
     * @param secretKey 密钥
     * @return 签名结果，包含签名值、时间戳和随机数
     */
    public CryptoResponse generateSign(Map<String, Object> params, String secretKey) {
        long timestamp = System.currentTimeMillis();
        String nonce = java.util.UUID.randomUUID().toString().replace("-", "");
        String sign = SignUtil.generateSign(params, secretKey, timestamp, nonce);
        return CryptoResponse.builder().sign(sign).timestamp(timestamp).nonce(nonce).build();
    }

    /**
     * 验证签名。
     *
     * @param params    参数集合
     * @param secretKey 密钥
     * @param sign      签名值
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 验证结果
     */
    public CryptoResponse verifySign(Map<String, Object> params, String secretKey,
                                     String sign, long timestamp, String nonce) {
        boolean verified = SignUtil.verifySign(params, sign, secretKey, timestamp, nonce);
        return CryptoResponse.builder().verified(verified).sign(sign).build();
    }

    /**
     * XSS清洗。
     *
     * @param content 待清洗内容
     * @return 清洗后的内容
     */
    public CryptoResponse xssClean(String content) {
        String cleaned = XssCleaner.cleanText(content);
        return CryptoResponse.builder().original(content).result(cleaned).processed(cleaned).build();
    }
}
