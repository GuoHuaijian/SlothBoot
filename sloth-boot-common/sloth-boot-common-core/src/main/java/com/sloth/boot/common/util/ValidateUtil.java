package com.sloth.boot.common.util;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * <p>
 * 提供常用的数据格式校验方法。所有方法对 null 输入均返回 {@code false}。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ValidateUtil {

    private ValidateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

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
     * 中文字符正则表达式
     */
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");

    /**
     * IPv4 正则表达式
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    /**
     * IPv6 正则表达式
     */
    private static final Pattern IPV6_PATTERN = Pattern.compile(
        "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::([0-9a-fA-F]{1,4}:){0,5}[0-9a-fA-F]{1,4}$|^([0-9a-fA-F]{1,4}:){1,7}:$");

    /**
     * UUID 正则表达式
     */
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    /**
     * MAC 地址正则表达式
     */
    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile(
        "^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$");

    /**
     * 中国车牌号正则表达式
     */
    private static final Pattern PLATE_NUMBER_PATTERN = Pattern.compile(
        "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤川青藏琼宁][A-HJ-NP-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$");

    /**
     * 中国座机号正则表达式
     */
    private static final Pattern LANDLINE_PATTERN = Pattern.compile(
        "^0\\d{2,3}-?\\d{7,8}$");

    /**
     * 用户名正则表达式（4-20位字母数字下划线）
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    /**
     * 强密码正则表达式（8位以上，含大小写字母、数字、特殊字符中的至少三种）
     */
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?![a-zA-Z]+$)(?!\\d+$)(?![!@#$%^&*_]+$)[a-zA-Z\\d!@#$%^&*_]{8,}$");

    /**
     * 域名正则表达式
     */
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
        "^(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.[A-Za-z0-9-]{1,63})*(\\.[A-Za-z]{2,})$");

    /**
     * 中文姓名正则表达式（2-20个汉字）
     */
    private static final Pattern CHINESE_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5]{2,20}$");

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
     * 身份证校验码加权因子
     */
    private static final int[] ID_CARD_WEIGHT = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 身份证校验码对应值
     */
    private static final char[] ID_CARD_CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * 判断是否为手机号
     *
     * @param mobile 手机号
     * @return 是否为手机号
     */
    public static boolean isMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

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
     * 判断是否为身份证号（18位格式校验）
     *
     * @param idCard 身份证号
     * @return 是否为身份证号
     */
    public static boolean isIdCard(String idCard) {
        return idCard != null && ID_CARD_PATTERN.matcher(idCard).matches();
    }

    /**
     * 判断是否为 URL
     *
     * @param url URL
     * @return 是否为 URL
     */
    public static boolean isUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    /**
     * 判断是否包含中文字符
     *
     * @param str 字符串
     * @return 是否包含中文字符
     */
    public static boolean isChinese(String str) {
        return str != null && CHINESE_PATTERN.matcher(str).matches();
    }

    /**
     * 判断是否为 IPv4 地址
     *
     * @param ip IP 地址
     * @return 是否为 IPv4 地址
     */
    public static boolean isIpv4(String ip) {
        return ip != null && IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 判断是否为 IPv6 地址
     *
     * @param ip IP 地址
     * @return 是否为 IPv6 地址
     */
    public static boolean isIpv6(String ip) {
        return ip != null && IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * 判断是否为 IP 地址（IPv4 或 IPv6）
     *
     * @param ip IP 地址
     * @return 是否为 IP 地址
     */
    public static boolean isIp(String ip) {
        return isIpv4(ip) || isIpv6(ip);
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
     * 判断是否为 MAC 地址
     *
     * @param mac MAC 地址
     * @return 是否为 MAC 地址
     */
    public static boolean isMacAddress(String mac) {
        return mac != null && MAC_ADDRESS_PATTERN.matcher(mac).matches();
    }

    /**
     * 判断是否为中国大陆车牌号
     * <p>
     * 支持普通车牌、新能源车牌、挂车、学车、警车、港澳车牌。
     * <pre>
     * 京A12345  → true
     * 沪ABC123  → true
     * 粤B1234挂 → true
     * </pre>
     *
     * @param plateNumber 车牌号
     * @return 是否为车牌号
     */
    public static boolean isPlateNumber(String plateNumber) {
        return plateNumber != null && PLATE_NUMBER_PATTERN.matcher(plateNumber).matches();
    }

    /**
     * 判断是否为中国大陆座机号
     *
     * @param landline 座机号
     * @return 是否为座机号
     */
    public static boolean isLandline(String landline) {
        return landline != null && LANDLINE_PATTERN.matcher(landline).matches();
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
        if (Pattern.compile("[A-Z]").matcher(password).find()) {
            typeCount++;
        }
        if (Pattern.compile("[a-z]").matcher(password).find()) {
            typeCount++;
        }
        if (Pattern.compile("\\d").matcher(password).find()) {
            typeCount++;
        }
        if (Pattern.compile("[!@#$%^&*_+=\\-]").matcher(password).find()) {
            typeCount++;
        }
        return typeCount >= 3;
    }

    /**
     * 判断是否为合法端口号（1-65535）
     *
     * @param port 端口号字符串
     * @return 是否为合法端口号
     */
    public static boolean isPort(String port) {
        if (port == null || port.isEmpty()) {
            return false;
        }
        if (!NUMERIC_PATTERN.matcher(port).matches()) {
            return false;
        }
        try {
            int portNum = Integer.parseInt(port);
            return portNum >= 1 && portNum <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断是否为合法域名
     *
     * @param domain 域名
     * @return 是否为合法域名
     */
    public static boolean isDomain(String domain) {
        return domain != null && DOMAIN_PATTERN.matcher(domain).matches();
    }

    /**
     * 判断是否为合法中文姓名（2-20个汉字）
     *
     * @param name 姓名
     * @return 是否为合法中文姓名
     */
    public static boolean isChineseName(String name) {
        return name != null && CHINESE_NAME_PATTERN.matcher(name).matches();
    }

    /**
     * 判断是否为合法18位身份证号（含校验位验证）
     * <p>
     * 校验规则：
     * <ol>
     *   <li>前 17 位为数字，第 18 位可以是数字或 X</li>
     *   <li>按加权因子对前 17 位加权求和</li>
     *   <li>对 11 取模得到校验码索引，与第 18 位比较</li>
     * </ol>
     *
     * @param idCard 身份证号
     * @return 是否为合法身份证号
     */
    public static boolean isIdCardWithChecksum(String idCard) {
        if (!isIdCard(idCard)) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * ID_CARD_WEIGHT[i];
        }
        char expectedCheckCode = ID_CARD_CHECK_CODES[sum % 11];
        return Character.toUpperCase(idCard.charAt(17)) == expectedCheckCode;
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
}
