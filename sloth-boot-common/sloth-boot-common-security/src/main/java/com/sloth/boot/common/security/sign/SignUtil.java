package com.sloth.boot.common.security.sign;

import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.common.security.crypto.HashUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class SignUtil {

    /**
     * 生成签名
     *
     * @param params       参数Map
     * @param secretKey    密钥
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @return 签名
     */
    public static String generateSign(Map<String, Object> params, String secretKey, long timestamp, String nonce) {
        // 将参数按字典序排序
        TreeMap<String, Object> sortedParams = new TreeMap<>(params);

        // 构建待签名字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 添加时间戳、随机数和密钥
        sb.append("&timestamp=").append(timestamp)
          .append("&nonce=").append(nonce)
          .append("&secret=").append(secretKey);

        // SHA256 哈希
        return HashUtil.sha256(sb.toString());
    }

    /**
     * 验证签名
     *
     * @param params       参数Map
     * @param sign         签名
     * @param secretKey    密钥
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @return 是否验证成功
     */
    public static boolean verifySign(Map<String, Object> params, String sign, String secretKey, long timestamp, String nonce) {
        String generatedSign = generateSign(params, secretKey, timestamp, nonce);
        return generatedSign.equals(sign);
    }

    /**
     * 从 JSON 字符串生成签名
     *
     * @param json        JSON 字符串
     * @param secretKey   密钥
     * @param timestamp   时间戳
     * @param nonce       随机数
     * @return 签名
     */
    public static String generateSignFromJson(String json, String secretKey, long timestamp, String nonce) {
        Map<String, Object> params = JsonUtil.parseObject(json, Map.class);
        return generateSign(params, secretKey, timestamp, nonce);
    }

    /**
     * 从 JSON 字符串验证签名
     *
     * @param json        JSON 字符串
     * @param sign        签名
     * @param secretKey   密钥
     * @param timestamp   时间戳
     * @param nonce       随机数
     * @return 是否验证成功
     */
    public static boolean verifySignFromJson(String json, String sign, String secretKey, long timestamp, String nonce) {
        Map<String, Object> params = JsonUtil.parseObject(json, Map.class);
        return verifySign(params, sign, secretKey, timestamp, nonce);
    }
}