package com.sloth.boot.common.util;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * <p>
 * 提供常用的数据格式校验方法，包括邮箱、用户名、强密码、银行卡号、UUID 等。
 * 所有方法对 null 输入均返回 {@code false}。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ValidateUtil {

    private ValidateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 邮箱正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * 用户名正则表达式（4-20位字母数字下划线）
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    /**
     * 强密码正则表达式（8位以上，含大小写字母、数字、特殊字符中的至少三种）
     */
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?![a-zA-Z]+$)(?!\\d+$)(?![!@#$%^&*_]+$)[a-zA-Z\\d!@#$%^&*_]{8,}$");

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*_+=\\-]");

    /**
     * UUID 正则表达式
     */
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    /**
     * 数字正则表达式
     */
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");

    /**
     * 字母正则表达式
     */
    private static final Pattern ALPHA_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    /**
     * 字母数字正则表达式
     */
    private static final Pattern ALPHA_NUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    /**
     * 判断是否为邮箱
     *
     * @param email 邮箱
     * @return 是否为邮箱
     */
    public static boolean isEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 判断是否为合法用户名（4-20位字母数字下划线）
     *
     * @param username 用户名
     * @return 是否为合法用户名
     */
    public static boolean isUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 判断是否为强密码
     * <p>
     * 要求 8 位以上，且至少包含大写字母、小写字母、数字、特殊字符中的三种。
     *
     * @param password 密码
     * @return 是否为强密码
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        int typeCount = 0;
        if (UPPERCASE_PATTERN.matcher(password).find()) {
            typeCount++;
        }
        if (LOWERCASE_PATTERN.matcher(password).find()) {
            typeCount++;
        }
        if (DIGIT_PATTERN.matcher(password).find()) {
            typeCount++;
        }
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            typeCount++;
        }
        return typeCount >= 3;
    }

    /**
     * 判断是否为合法银行卡号（Luhn 算法校验）
     * <p>
     * Luhn 算法步骤：
     * <ol>
     *   <li>从右向左，对偶数位数字乘以 2（如果结果大于 9 则减 9）</li>
     *   <li>将所有数字求和</li>
     *   <li>如果总和能被 10 整除，则卡号合法</li>
     * </ol>
     * <pre>
     * ValidateUtil.isBankCard("6222020200011111111") → true/false（取决于卡号）
     * </pre>
     *
     * @param bankCard 银行卡号
     * @return 是否为合法银行卡号
     */
    public static boolean isBankCard(String bankCard) {
        if (bankCard == null || bankCard.isEmpty()) {
            return false;
        }
        if (!NUMERIC_PATTERN.matcher(bankCard).matches()) {
            return false;
        }
        int length = bankCard.length();
        if (length < 13 || length > 19) {
            return false;
        }
        int sum = 0;
        boolean alternate = false;
        for (int i = length - 1; i >= 0; i--) {
            int n = bankCard.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    /**
     * 判断是否全为数字
     *
     * @param str 字符串
     * @return 是否全为数字
     */
    public static boolean isNumeric(String str) {
        return str != null && NUMERIC_PATTERN.matcher(str).matches();
    }

    /**
     * 判断是否全为字母
     *
     * @param str 字符串
     * @return 是否全为字母
     */
    public static boolean isAlpha(String str) {
        return str != null && ALPHA_PATTERN.matcher(str).matches();
    }

    /**
     * 判断是否全为字母和数字
     *
     * @param str 字符串
     * @return 是否全为字母和数字
     */
    public static boolean isAlphaNumeric(String str) {
        return str != null && ALPHA_NUMERIC_PATTERN.matcher(str).matches();
    }

    /**
     * 判断是否为 UUID 格式
     *
     * @param str 字符串
     * @return 是否为 UUID
     */
    public static boolean isUuid(String str) {
        return str != null && UUID_PATTERN.matcher(str).matches();
    }

    /**
     * 通用正则匹配
     *
     * @param str   待匹配字符串
     * @param regex 正则表达式
     * @return 是否匹配
     */
    public static boolean matches(String str, String regex) {
        if (str == null || regex == null) {
            return false;
        }
        return Pattern.matches(regex, str);
    }
}
