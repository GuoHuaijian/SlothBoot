package com.sloth.boot.example.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.dto.CryptoRequest;
import com.sloth.boot.example.dto.CryptoResponse;
import com.sloth.boot.example.service.SecurityDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityDemoService securityService;

    @PostMapping("/aes/encrypt")
    public R<CryptoResponse> aesEncrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityService.aesEncrypt(request.getData(), request.getKey(), request.getIv()));
    }

    @PostMapping("/aes/decrypt")
    public R<CryptoResponse> aesDecrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityService.aesDecrypt(request.getData(), request.getKey(), request.getIv()));
    }

    @PostMapping("/rsa/generate-keypair")
    public R<CryptoResponse> rsaGenerateKeypair() {
        return R.ok(securityService.rsaGenerateKeypair());
    }

    @PostMapping("/rsa/encrypt")
    public R<CryptoResponse> rsaEncrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityService.rsaEncrypt(request.getData(), request.getPublicKey()));
    }

    @PostMapping("/rsa/decrypt")
    public R<CryptoResponse> rsaDecrypt(@RequestBody CryptoRequest request) {
        return R.ok(securityService.rsaDecrypt(request.getData(), request.getPrivateKey()));
    }

    @PostMapping("/rsa/sign")
    public R<CryptoResponse> rsaSign(@RequestBody CryptoRequest request) {
        return R.ok(securityService.rsaSign(request.getData(), request.getPrivateKey()));
    }

    @PostMapping("/rsa/verify")
    public R<CryptoResponse> rsaVerify(@RequestBody CryptoRequest request) {
        return R.ok(securityService.rsaVerify(request.getData(), request.getSign(), request.getPublicKey()));
    }

    @PostMapping("/hash/bcrypt")
    public R<CryptoResponse> bcryptHash(@RequestBody CryptoRequest request) {
        return R.ok(securityService.bcryptHash(request.getData()));
    }

    @PostMapping("/hash/verify")
    public R<CryptoResponse> bcryptVerify(@RequestBody CryptoRequest request) {
        return R.ok(securityService.bcryptVerify(request.getData(), request.getSign()));
    }

    @PostMapping("/hash/sha256")
    public R<CryptoResponse> sha256(@RequestBody CryptoRequest request) {
        return R.ok(securityService.sha256(request.getData()));
    }

    @PostMapping("/sign/generate")
    public R<CryptoResponse> generateSign(@RequestBody CryptoRequest request) {
        return R.ok(securityService.generateSign(request.getParams(), request.getSecretKey()));
    }

    @PostMapping("/sign/verify")
    public R<CryptoResponse> verifySign(@RequestBody CryptoRequest request) {
        return R.ok(securityService.verifySign(request.getParams(), request.getSign(), request.getSecretKey(), request.getTimestamp(), request.getNonce()));
    }

    @PostMapping("/xss/clean")
    public R<CryptoResponse> cleanXss(@RequestBody CryptoRequest request) {
        return R.ok(securityService.cleanXss(request.getContent()));
    }
}
