package com.sloth.boot.example.controller.security;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.model.security.request.CryptoRequest;
import com.sloth.boot.example.model.security.vo.CryptoResponse;
import com.sloth.boot.example.service.security.SecurityDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 安全工具演示接口
 * <p>
 * 演示 AES/RSA 加解密、BCrypt/SHA-256 哈希、HMAC 请求签名、XSS 清洗等安全能力
 */
@Tag(name = "安全工具", description = "演示 AES/RSA/SM4 加解密、哈希、签名、XSS 清洗等安全能力")
@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityDemoService securityService;

    @Operation(summary = "AES 加密", description = "使用 AES 算法对数据进行加密")
    @PostMapping("/aes/encrypt")
    public R<CryptoResponse> aesEncrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityService.aesEncrypt(request.getData(), request.getKey(), request.getIv()));
    }

    @Operation(summary = "AES 解密", description = "使用 AES 算法对数据进行解密")
    @PostMapping("/aes/decrypt")
    public R<CryptoResponse> aesDecrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityService.aesDecrypt(request.getData(), request.getKey(), request.getIv()));
    }

    @Operation(summary = "RSA 生成密钥对", description = "生成 RSA 非对称加密的公钥和私钥对")
    @PostMapping("/rsa/generate-keypair")
    public R<CryptoResponse> rsaGenerateKeypair() {
        return R.ok(securityService.rsaGenerateKeypair());
    }

    @Operation(summary = "RSA 公钥加密", description = "使用 RSA 公钥对数据进行加密")
    @PostMapping("/rsa/encrypt")
    public R<CryptoResponse> rsaEncrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityService.rsaEncrypt(request.getData(), request.getPublicKey()));
    }

    @Operation(summary = "RSA 私钥解密", description = "使用 RSA 私钥对数据进行解密")
    @PostMapping("/rsa/decrypt")
    public R<CryptoResponse> rsaDecrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityService.rsaDecrypt(request.getData(), request.getPrivateKey()));
    }

    @Operation(summary = "RSA 签名", description = "使用 RSA 私钥对数据进行签名")
    @PostMapping("/rsa/sign")
    public R<CryptoResponse> rsaSign(@RequestBody CryptoRequest request) {
        return R.ok(securityService.rsaSign(request.getData(), request.getPrivateKey()));
    }

    @Operation(summary = "RSA 验签", description = "使用 RSA 公钥验证数据签名")
    @PostMapping("/rsa/verify")
    public R<CryptoResponse> rsaVerify(@RequestBody CryptoRequest request) {
        return R.ok(securityService.rsaVerify(request.getData(), request.getSign(), request.getPublicKey()));
    }

    @Operation(summary = "BCrypt 哈希", description = "使用 BCrypt 算法对密码进行哈希")
    @PostMapping("/hash/bcrypt")
    public R<CryptoResponse> bcryptHash(@RequestBody CryptoRequest request) {
        return R.ok(securityService.bcryptHash(request.getData()));
    }

    @Operation(summary = "BCrypt 验证", description = "验证密码与 BCrypt 哈希是否匹配")
    @PostMapping("/hash/verify")
    public R<CryptoResponse> bcryptVerify(@RequestBody CryptoRequest request) {
        return R.ok(securityService.bcryptVerify(request.getData(), request.getSign()));
    }

    @Operation(summary = "SHA-256 摘要", description = "计算数据的 SHA-256 哈希摘要")
    @PostMapping("/hash/sha256")
    public R<CryptoResponse> sha256(@RequestBody CryptoRequest request) {
        return R.ok(securityService.sha256(request.getData()));
    }

    @Operation(summary = "生成请求签名", description = "为请求参数生成签名（自动生成 timestamp 和 nonce）")
    @PostMapping("/sign/generate")
    public R<CryptoResponse> generateSign(@RequestBody CryptoRequest request) {
        return R.ok(securityService.generateSign(request.getParams(), request.getSecretKey()));
    }

    @Operation(summary = "验证请求签名", description = "验证请求参数的签名是否正确")
    @PostMapping("/sign/verify")
    public R<CryptoResponse> verifySign(@RequestBody CryptoRequest request) {
        return R.ok(securityService.verifySign(request.getParams(), request.getSign(), request.getSecretKey(), request.getTimestamp(), request.getNonce()));
    }

    @Operation(summary = "XSS 清洗", description = "对输入内容进行 XSS 攻击代码清洗")
    @PostMapping("/xss/clean")
    public R<CryptoResponse> cleanXss(@RequestBody CryptoRequest request) {
        return R.ok(securityService.cleanXss(request.getContent()));
    }
}
