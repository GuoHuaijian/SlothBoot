package com.sloth.boot.example.model.security.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 加解密请求参数
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "加解密请求参数")
public class CryptoRequest {

    @Schema(description = "待处理的数据", example = "Hello World")
    private String data;

    @Schema(description = "AES 密钥", example = "0123456789abcdef")
    private String key;

    @Schema(description = "AES 初始化向量", example = "0123456789abcdef")
    private String iv;

    @Schema(description = "RSA 公钥（Base64编码）")
    private String publicKey;

    @Schema(description = "RSA 私钥（Base64编码）")
    private String privateKey;

    @Schema(description = "签名值", example = "a1b2c3d4...")
    private String sign;

    @Schema(description = "签名参数（键值对）")
    private Map<String, Object> params;

    @Schema(description = "时间戳", example = "1700000000000")
    private Long timestamp;

    @Schema(description = "随机字符串", example = "abc123")
    private String nonce;

    @Schema(description = "签名密钥", example = "my-secret-key")
    private String secretKey;

    @Schema(description = "XSS 清洗内容", example = "<script>alert(1)</script>")
    private String content;
}
