package com.sloth.boot.common.security.crypto;

import com.sloth.boot.common.exception.SystemException;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES 对称加解密工具类。
 * <p>
 * 推荐使用 {@link #encryptGcm(String, String)} / {@link #decryptGcm(String, String)}：
 * GCM 模式自带完整性认证，IV 由本类随机生成并拼接到密文前，无需调用方管理。
 * <p>
 * 旧版 CBC 模式（{@link #encrypt(String, String, String)}）无完整性校验且依赖调用方
 * 自备 IV，仅用于兼容存量数据，新代码不应使用。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class AESUtil {

    private AESUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * AES/CBC/PKCS5Padding 算法（旧版兼容）
     */
    private static final String CBC_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * AES/GCM/NoPadding 算法（推荐）
     */
    private static final String GCM_ALGORITHM = "AES/GCM/NoPadding";
    /**
     * GCM 模式 IV 长度（字节），NIST SP 800-38D 推荐 12 字节
     */
    private static final int GCM_IV_LENGTH = 12;

    /**
     * GCM 认证标签长度（位）
     */
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * AES-GCM 加密。
     * <p>
     * 随机生成 IV 并拼接到密文前，输出格式为 {@code Base64(IV + ciphertext + tag)}，
     * 自带防篡改认证。
     *
     * @param data 待加密数据
     * @param key  密钥（16/24/32 字节）
     * @return Base64 编码的加密数据
     * @throws RuntimeException 加密失败时抛出
     */
    public static String encryptGcm(String data, String key) {
        byte[] keyBytes = requireValidKey(key);
        byte[] iv = new byte[GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance(GCM_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] ivAndCiphertext = ByteBuffer.allocate(iv.length + encrypted.length)
                .put(iv)
                .put(encrypted)
                .array();
            return Base64.getEncoder().encodeToString(ivAndCiphertext);
        } catch (Exception e) {
            throw SystemException.of("AES-GCM encryption failed", e);
        }
    }

    /**
     * AES-GCM 解密。
     * <p>
     * 输入为 {@link #encryptGcm(String, String)} 的输出。数据被篡改或密钥不匹配时抛出异常。
     *
     * @param data Base64 编码的加密数据
     * @param key  密钥（16/24/32 字节）
     * @return 解密后的原始数据
     * @throws RuntimeException 解密失败或校验不通过时抛出
     */
    public static String decryptGcm(String data, String key) {
        byte[] keyBytes = requireValidKey(key);
        try {
            byte[] ivAndCiphertext = Base64.getDecoder().decode(data);
            if (ivAndCiphertext.length <= GCM_IV_LENGTH) {
                throw SystemException.of("AES-GCM ciphertext too short");
            }
            byte[] iv = Arrays.copyOfRange(ivAndCiphertext, 0, GCM_IV_LENGTH);
            byte[] ciphertext = Arrays.copyOfRange(ivAndCiphertext, GCM_IV_LENGTH, ivAndCiphertext.length);
            Cipher cipher = Cipher.getInstance(GCM_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] decrypted = cipher.doFinal(ciphertext);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw SystemException.of("AES-GCM decryption failed", e);
        }
    }

    /**
     * AES 加密（旧版 CBC 模式）。
     *
     * @param data 待加密数据
     * @param key  密钥（16 字节）
     * @param iv   偏移量（16 字节），禁止复用固定值
     * @return Base64 编码的加密数据
     * @throws RuntimeException 加密失败时抛出
     * @deprecated 无完整性校验且 IV 由调用方管理，易被误用；请改用 {@link #encryptGcm(String, String)}
     */
    @Deprecated(since = "1.0.0")
    public static String encrypt(String data, String key, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(CBC_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw SystemException.of("AES encryption failed", e);
        }
    }

    /**
     * AES 解密（旧版 CBC 模式）。
     *
     * @param data Base64 编码的加密数据
     * @param key  密钥（16 字节）
     * @param iv   偏移量（16 字节）
     * @return 解密后的原始数据
     * @throws RuntimeException 解密失败时抛出
     * @deprecated 无完整性校验；请改用 {@link #decryptGcm(String, String)}
     */
    @Deprecated(since = "1.0.0")
    public static String decrypt(String data, String key, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(CBC_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw SystemException.of("AES decryption failed", e);
        }
    }

    /**
     * 校验 AES 密钥长度（16/24/32 字节）。
     *
     * @param key Base64 外的原始密钥字符串
     * @return 密钥字节数组
     */
    private static byte[] requireValidKey(String key) {
        if (key == null) {
            throw SystemException.of("AES key must not be null");
        }
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        int length = keyBytes.length;
        if (length != 16 && length != 24 && length != 32) {
            throw SystemException.of("AES key must be 16/24/32 bytes, got: " + length);
        }
        return keyBytes;
    }
}
