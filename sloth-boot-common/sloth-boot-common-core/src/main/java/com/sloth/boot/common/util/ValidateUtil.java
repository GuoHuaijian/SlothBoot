package com.sloth.boot.common.util;

import java.util.regex.Pattern;

/**
 * 验证工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class ValidateUtil {

    /**
     * 手机号正则表达式
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 邮箱正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * 身份证号正则表达式（18位）
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");

    /**
     * URL正则表达式
     */
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    /**
     * 中文名称正则表达式
     */
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");

    /**
     * 判断是否为手机号
     *
     * @param mobile 手机号
     * @return 是否为手机号
     */
    public static boolean isMobile(String mobile) {
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * 判断是否为邮箱
     *
     * @param email 邮箱
     * @return 是否为邮箱
     */
    public static boolean isEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 判断是否为身份证号
     *
     * @param idCard 身份证号
     * @return 是否为身份证号
     */
    public static boolean isIdCard(String idCard) {
        return ID_CARD_PATTERN.matcher(idCard).matches();
    }

    /**
     * 判断是否为 URL
     *
     * @param url URL
     * @return 是否为 URL
     */
    public static boolean isUrl(String url) {
        return URL_PATTERN.matcher(url).matches();
    }

    /**
     * 判断是否为中文名称
     *
     * @param chinese 中文名称
     * @return 是否为中文名称
     */
    public static boolean isChinese(String chinese) {
        return CHINESE_PATTERN.matcher(chinese).matches();
    }
}
