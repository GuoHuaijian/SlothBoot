package com.sloth.boot.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数字工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class NumberUtil {

    /**
     * 加法运算
     *
     * @param a 加数
     * @param b 加数
     * @return 和
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    /**
     * 减法运算
     *
     * @param a 被减数
     * @param b 减数
     * @return 差
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    /**
     * 乘法运算
     *
     * @param a 乘数
     * @param b 乘数
     * @return 积
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    /**
     * 除法运算
     *
     * @param a 被除数
     * @param b 除数
     * @return 商
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        return divide(a, b, 2);
    }

    /**
     * 除法运算
     *
     * @param a     被除数
     * @param b     除数
     * @param scale 小数位数
     * @return 商
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale) {
        return a.divide(b, scale, RoundingMode.HALF_UP);
    }

    /**
     * 四舍五入
     *
     * @param number 数字
     * @param scale  小数位数
     * @return 四舍五入后的数字
     */
    public static BigDecimal roundHalfUp(BigDecimal number, int scale) {
        return number.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 格式化金额（保留2位小数）
     *
     * @param number 金额
     * @return 格式化后的金额字符串
     */
    public static String formatMoney(BigDecimal number) {
        return number.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * 元转分
     *
     * @param yuan 元
     * @return 分
     */
    public static Long yuan2Fen(BigDecimal yuan) {
        return yuan.multiply(new BigDecimal(100)).longValue();
    }

    /**
     * 分转元
     *
     * @param fen 分
     * @return 元
     */
    public static BigDecimal fen2Yuan(Long fen) {
        return new BigDecimal(fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }
}
