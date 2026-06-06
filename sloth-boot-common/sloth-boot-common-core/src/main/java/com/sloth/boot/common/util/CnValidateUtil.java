package com.sloth.boot.common.util;

import java.util.regex.Pattern;

/**
 * 中国相关验证工具类
 * <p>
 * 提供中国大陆特有数据格式的校验方法，包括手机号、身份证号、车牌号、座机号、中文姓名等。
 * 所有方法对 null 输入均返回 {@code false}。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class CnValidateUtil {

    private CnValidateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 手机号正则表达式
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 身份证号正则表达式（18位）
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");

    /**
     * 中文字符正则表达式
     */
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");

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
     * 中文姓名正则表达式（2-20个汉字）
     */
    private static final Pattern CHINESE_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5]{2,20}$");

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
     * 判断是否为身份证号（18位格式校验）
     *
     * @param idCard 身份证号
     * @return 是否为身份证号
     */
    public static boolean isIdCard(String idCard) {
        return idCard != null && ID_CARD_PATTERN.matcher(idCard).matches();
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
     * 判断是否为合法中文姓名（2-20个汉字）
     *
     * @param name 姓名
     * @return 是否为合法中文姓名
     */
    public static boolean isChineseName(String name) {
        return name != null && CHINESE_NAME_PATTERN.matcher(name).matches();
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
}
