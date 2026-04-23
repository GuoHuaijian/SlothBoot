package com.sloth.boot.common.security.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class Base64Util {

    /**
     * Base64 编码
     *
     * @param data 待编码数据
     * @return 编码后的数据
     */
    public static String encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 解码
     *
     * @param data 待解码数据
     * @return 解码后的数据
     */
    public static String decode(String data) {
        byte[] decoded = Base64.getDecoder().decode(data);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    /**
     * Base64 URL 安全编码
     *
     * @param data 待编码数据
     * @return 编码后的数据
     */
    public static String encodeUrlSafe(String data) {
        return Base64.getUrlEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 URL 安全解码
     *
     * @param data 待解码数据
     * @return 解码后的数据
     */
    public static String decodeUrlSafe(String data) {
        byte[] decoded = Base64.getUrlDecoder().decode(data);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}