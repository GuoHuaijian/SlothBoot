package com.sloth.boot.example.model.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 加解密响应结果
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
@Schema(description = "加解密响应结果")
public class CryptoResponse {

    @Schema(description = "处理结果", example = "加密后的Base64字符串")
    private String result;

    @Schema(description = "原始数据", example = "Hello World")
    private String original;

    @Schema(description = "处理后数据", example = "U2FsdGVkX1...")
    private String processed;

    @Schema(description = "处理耗时（毫秒）", example = "5")
    private Long costMs;

    @Schema(description = "验签结果", example = "true")
    private Boolean verified;

    @Schema(description = "RSA 公钥（Base64编码）")
    private String publicKey;

    @Schema(description = "RSA 私钥（Base64编码）")
    private String privateKey;

    @Schema(description = "签名值", example = "a1b2c3d4...")
    private String sign;

    @Schema(description = "时间戳", example = "1700000000000")
    private Long timestamp;

    @Schema(description = "随机字符串", example = "abc123")
    private String nonce;
}
