package com.sloth.boot.example.application.model.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加解密响应。
 * <p>
 * 用于接口返回加解密操作的结果信息。
 * 包含加密结果、解密结果、验签结果、耗时等信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "加解密响应")
public class CryptoResponse {

    /** 主要结果（加密后的密文、解密后的明文等） */
    @Schema(description = "主要结果", example = "a1b2c3d4...")
    private String result;

    /** 原始输入 */
    @Schema(description = "原始输入", example = "Hello World")
    private String original;

    /** 处理结果 */
    @Schema(description = "处理结果", example = "encrypted_data...")
    private String processed;

    /** 执行耗时（毫秒） */
    @Schema(description = "执行耗时（毫秒）", example = "15")
    private Long costMs;

    /** 验签/验证结果 */
    @Schema(description = "验签/验证结果", example = "true")
    private Boolean verified;

    /** RSA公钥（密钥生成时返回） */
    @Schema(description = "RSA公钥")
    private String publicKey;

    /** RSA私钥（密钥生成时返回） */
    @Schema(description = "RSA私钥")
    private String privateKey;

    /** 签名值 */
    @Schema(description = "签名值")
    private String sign;

    /** 时间戳 */
    @Schema(description = "时间戳")
    private Long timestamp;

    /** 随机数 */
    @Schema(description = "随机数")
    private String nonce;
}
