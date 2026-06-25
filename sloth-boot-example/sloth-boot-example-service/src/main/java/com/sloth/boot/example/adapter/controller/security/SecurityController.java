package com.sloth.boot.example.adapter.controller.security;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.security.SecurityCommand;
import com.sloth.boot.example.application.model.form.security.CryptoRequest;
import com.sloth.boot.example.application.model.vo.security.CryptoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 加解密演示接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "加解密", description = "AES/RSA/BCrypt/SHA256 签名验签")
@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityCommand securityCommand;

    @Operation(summary = "AES加密")
    @PostMapping("/aes/encrypt")
    public R<CryptoResponse> aesEncrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.aesEncrypt(request.getData(), request.getKey(), request.getIv()));
    }

    @Operation(summary = "AES解密")
    @PostMapping("/aes/decrypt")
    public R<CryptoResponse> aesDecrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.aesDecrypt(request.getData(), request.getKey(), request.getIv()));
    }

    @Operation(summary = "RSA生成密钥对")
    @PostMapping("/rsa/keypair")
    public R<CryptoResponse> rsaKeyPair() {
        return R.ok(securityCommand.generateRsaKeyPair());
    }

    @Operation(summary = "RSA加密")
    @PostMapping("/rsa/encrypt")
    public R<CryptoResponse> rsaEncrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.rsaEncrypt(request.getData(), request.getPublicKey()));
    }

    @Operation(summary = "RSA解密")
    @PostMapping("/rsa/decrypt")
    public R<CryptoResponse> rsaDecrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.rsaDecrypt(request.getData(), request.getPrivateKey()));
    }

    @Operation(summary = "RSA签名")
    @PostMapping("/rsa/sign")
    public R<CryptoResponse> rsaSign(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.rsaSign(request.getData(), request.getPrivateKey()));
    }

    @Operation(summary = "RSA验签")
    @PostMapping("/rsa/verify")
    public R<CryptoResponse> rsaVerify(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.rsaVerify(request.getData(), request.getSign(), request.getPublicKey()));
    }

    @Operation(summary = "BCrypt哈希")
    @PostMapping("/bcrypt/hash")
    public R<CryptoResponse> bcryptHash(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.bcryptHash(request.getData()));
    }

    @Operation(summary = "BCrypt验证")
    @PostMapping("/bcrypt/verify")
    public R<CryptoResponse> bcryptVerify(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.bcryptVerify(request.getData(), request.getSign()));
    }

    @Operation(summary = "SHA-256哈希")
    @PostMapping("/sha256")
    public R<CryptoResponse> sha256(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.sha256Hash(request.getData()));
    }

    @Operation(summary = "生成签名")
    @PostMapping("/sign/generate")
    public R<CryptoResponse> generateSign(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.generateSign(request.getParams(), request.getSecretKey()));
    }

    @Operation(summary = "验证签名")
    @PostMapping("/sign/verify")
    public R<CryptoResponse> verifySign(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.verifySign(request.getParams(), request.getSecretKey(),
            request.getSign(), request.getTimestamp(), request.getNonce()));
    }

    @Operation(summary = "XSS清洗")
    @PostMapping("/xss/clean")
    public R<CryptoResponse> xssClean(@RequestBody CryptoRequest request) {
        return R.ok(securityCommand.xssClean(request.getContent()));
    }
}
