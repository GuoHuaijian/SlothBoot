package com.sloth.boot.example.application.model.form.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 加解密请求。
 * <p>
 * 用于接收前端加解密操作的请求参数。
 * 包含AES、RSA、HMAC、XSS清洗等多种安全操作所需的参数。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "加解密请求")
public class CryptoRequest {

    /** 待加密/解密的内容 */
    @Schema(description = "待加密/解密的内容", example = "Hello SlothBoot")
    private String data;

    /** AES密钥（Base64编码，16/24/32字节） */
    @Schema(description = "AES密钥（Base64编码，16/24/32字节）", example = "U2xvdGhCb290MTIzNDU2Nzg5MDEyMzQ1Ng==")
    private String key;

    /** AES IV向量（CBC模式，Base64编码，16字节） */
    @Schema(description = "AES IV向量（CBC模式，Base64编码，16字节）", example = "c2xvdGhib290MTIz")
    private String iv;

    /** RSA公钥（Base64编码） */
    @Schema(description = "RSA公钥（Base64编码）", example = "MIIBIjANBgkqh...")
    private String publicKey;

    /** RSA私钥（Base64编码） */
    @Schema(description = "RSA私钥（Base64编码）", example = "MIIEvQIBADANBg...")
    private String privateKey;

    /** 签名值 */
    @Schema(description = "签名值", example = "a1b2c3d4e5f6...")
    private String sign;

    /** 待签名的参数（Map格式） */
    @Schema(description = "待签名的参数（Map格式）")
    private Map<String, Object> params;

    /** 时间戳（毫秒） */
    @Schema(description = "时间戳（毫秒）", example = "1749705600000")
    private Long timestamp;

    /** 随机数（32位） */
    @Schema(description = "随机数（32位）", example = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6")
    private String nonce;

    /** HMAC签名密钥 */
    @Schema(description = "HMAC签名密钥", example = "my-secret-key")
    private String secretKey;

    /** 待清洗的HTML内容 */
    @Schema(description = "待清洗的HTML内容", example = "<script>alert('xss')</script><b>正常内容</b>")
    private String content;
}
