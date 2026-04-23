package com.sloth.boot.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * 脱敏工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class DesensitizeUtil {

    /**
     * 手机号脱敏（保留前3后4）
     *
     * @param mobile 手机号
     * @return 脱敏后的手机号
     */
    public static String mobilePhone(String mobile) {
        if (StrUtil.isBlank(mobile) || mobile.length() != 11) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 身份证号脱敏（保留前4后4）
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String idCard(String idCard) {
        if (StrUtil.isBlank(idCard) || idCard.length() < 8) {
            return idCard;
        }
        int length = idCard.length();
        return idCard.replaceAll("(\\d{4})\\d{" + (length - 8) + "}(\\d{4})", "$1****$2");
    }

    /**
     * 邮箱脱敏（保留@前首字母）
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String email(String email) {
        if (StrUtil.isBlank(email)) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return email;
        }
        return email.replaceAll("(\\w{1})\\w*(\\@\\w+\\.\\w+)", "$1****$2");
    }

    /**
     * 银行卡号脱敏（保留后4位）
     *
     * @param bankCard 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String bankCard(String bankCard) {
        if (StrUtil.isBlank(bankCard) || bankCard.length() < 4) {
            return bankCard;
        }
        return bankCard.replaceAll("(\\d{4})\\d{0,10}(\\d{4})", "$1****$2");
    }

    /**
     * 中文名称脱敏（保留姓）
     *
     * @param chineseName 中文名称
     * @return 脱敏后的名称
     */
    public static String chineseName(String chineseName) {
        if (StrUtil.isBlank(chineseName) || chineseName.length() <= 1) {
            return chineseName;
        }
        return chineseName.replaceAll("(\\S)\\S*", "$1*");
    }

    /**
     * 地址脱敏
     *
     * @param address       地址
     * @param sensitiveSize 脱敏长度
     * @return 脱敏后的地址
     */
    public static String address(String address, int sensitiveSize) {
        if (StrUtil.isBlank(address) || address.length() <= sensitiveSize) {
            return address;
        }
        return address.replaceAll(".{" + sensitiveSize + "}(?!$)", "***");
    }

    /**
     * 自定义脱敏
     *
     * @param str        原始字符串
     * @param prefixLen  前缀保留长度
     * @param suffixLen  后缀保留长度
     * @return 脱敏后的字符串
     */
    public static String custom(String str, int prefixLen, int suffixLen) {
        if (StrUtil.isBlank(str) || str.length() <= prefixLen + suffixLen) {
            return str;
        }
        int length = str.length();
        String prefix = str.substring(0, prefixLen);
        String suffix = str.substring(length - suffixLen);
        return prefix + "*".repeat(length - prefixLen - suffixLen) + suffix;
    }
}
