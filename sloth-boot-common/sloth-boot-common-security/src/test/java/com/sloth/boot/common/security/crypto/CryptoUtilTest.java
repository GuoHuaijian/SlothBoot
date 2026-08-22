package com.sloth.boot.common.security.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 加解密工具测试。
 */
@DisplayName("AES/SM4/RSA 加解密测试")
class CryptoUtilTest {

    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("AES-CBC 旧接口加解密回环")
    void aesCbc_roundtrip() {
        String data = "sloth-boot";
        String encrypted = AESUtil.encrypt(data, "0123456789abcdef", "abcdef9876543210");
        assertThat(AESUtil.decrypt(encrypted, "0123456789abcdef", "abcdef9876543210")).isEqualTo(data);
    }

    @Test
    @DisplayName("AES-GCM 加解密回环，每次密文不同（随机 IV）")
    void aesGcm_roundtripAndRandomIv() {
        String data = "sloth-boot";
        String first = AESUtil.encryptGcm(data, "0123456789abcdef");
        String second = AESUtil.encryptGcm(data, "0123456789abcdef");

        assertThat(AESUtil.decryptGcm(first, "0123456789abcdef")).isEqualTo(data);
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("AES-GCM 密文被篡改时解密失败")
    void aesGcm_rejectsTamperedCiphertext() {
        String encrypted = AESUtil.encryptGcm("sloth-boot", "0123456789abcdef");
        byte[] bytes = java.util.Base64.getDecoder().decode(encrypted);
        bytes[bytes.length - 1] ^= 0x01;

        assertThatThrownBy(() -> AESUtil.decryptGcm(java.util.Base64.getEncoder().encodeToString(bytes), "0123456789abcdef"))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("AES-GCM 拒绝不合法长度的密钥")
    void aesGcm_rejectsInvalidKeyLength() {
        assertThatThrownBy(() -> AESUtil.encryptGcm("data", "short-key"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("16/24/32");
    }

    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("SM4-CBC 旧接口加解密回环")
    void sm4Cbc_roundtrip() {
        String encrypted = SM4Util.encrypt("sloth-boot", "0123456789abcdef", "abcdef9876543210");
        assertThat(SM4Util.decrypt(encrypted, "0123456789abcdef", "abcdef9876543210")).isEqualTo("sloth-boot");
    }

    @Test
    @DisplayName("SM4-GCM 加解密回环")
    void sm4Gcm_roundtrip() {
        String encrypted = SM4Util.encryptGcm("sloth-boot", "0123456789abcdef");
        assertThat(SM4Util.decryptGcm(encrypted, "0123456789abcdef")).isEqualTo("sloth-boot");
    }

    @Test
    @DisplayName("SM4-GCM 密文被篡改时解密失败")
    void sm4Gcm_rejectsTamperedCiphertext() {
        String encrypted = SM4Util.encryptGcm("sloth-boot", "0123456789abcdef");
        byte[] bytes = java.util.Base64.getDecoder().decode(encrypted);
        bytes[bytes.length - 1] ^= 0x01;

        assertThatThrownBy(() -> SM4Util.decryptGcm(java.util.Base64.getEncoder().encodeToString(bytes), "0123456789abcdef"))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("RSA-OAEP 加解密回环")
    void rsaOaep_roundtrip() {
        var keyPair = RSAUtil.generateKeyPair();
        String publicKey = RSAUtil.getPublicKey(keyPair);
        String privateKey = RSAUtil.getPrivateKey(keyPair);

        String encrypted = RSAUtil.encryptOaep("sloth-boot", publicKey);
        assertThat(RSAUtil.decryptOaep(encrypted, privateKey)).isEqualTo("sloth-boot");
    }

    @Test
    @DisplayName("RSA PKCS#1 v1.5 旧接口加解密回环")
    void rsaPkcs1_roundtrip() {
        var keyPair = RSAUtil.generateKeyPair();
        String publicKey = RSAUtil.getPublicKey(keyPair);
        String privateKey = RSAUtil.getPrivateKey(keyPair);

        String encrypted = RSAUtil.encrypt("sloth-boot", publicKey);
        assertThat(RSAUtil.decrypt(encrypted, privateKey)).isEqualTo("sloth-boot");
    }

    @Test
    @DisplayName("RSA 签名验签成功，非法签名返回 false 而非抛异常")
    void rsaSign_verifyAndTolerateMalformedSign() {
        var keyPair = RSAUtil.generateKeyPair();
        String publicKey = RSAUtil.getPublicKey(keyPair);
        String privateKey = RSAUtil.getPrivateKey(keyPair);

        String sign = RSAUtil.sign("sloth-boot", privateKey);
        assertThat(RSAUtil.verify("sloth-boot", sign, publicKey)).isTrue();
        assertThat(RSAUtil.verify("tampered", sign, publicKey)).isFalse();
        assertThat(RSAUtil.verify("sloth-boot", "not-base64-sign!!", publicKey)).isFalse();
    }
}
