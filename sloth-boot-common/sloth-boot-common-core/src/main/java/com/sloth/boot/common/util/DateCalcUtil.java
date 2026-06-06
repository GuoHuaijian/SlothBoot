package com.sloth.boot.common.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 日期计算工具类
 * <p>
 * 提供日期时间的加减运算与比较方法，基于 {@code java.time} API。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class DateCalcUtil {

    private DateCalcUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 增加天数
     *
     * @param dateTime 日期时间
     * @param days     天数
     * @return 增加后的日期时间
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }

    /**
     * 增加小时
     *
     * @param dateTime 日期时间
     * @param hours    小时数
     * @return 增加后的日期时间
     */
    public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
        return dateTime.plusHours(hours);
    }

    /**
     * 增加分钟
     *
     * @param dateTime 日期时间
     * @param minutes  分钟数
     * @return 增加后的日期时间
     */
    public static LocalDateTime plusMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime.plusMinutes(minutes);
    }

    /**
     * 减少天数
     *
     * @param dateTime 日期时间
     * @param days     天数
     * @return 减少后的日期时间
     */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        return dateTime.minusDays(days);
    }

    /**
     * 计算时间差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间差
     */
    public static Duration between(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end);
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数差（可为负数）
     */
    public static long betweenDays(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的天数差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 天数差（可为负数）
     */
    public static long betweenDays(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的小时差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 小时差（可为负数）
     */
    public static long betweenHours(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的分钟差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 分钟差（可为负数）
     */
    public static long betweenMinutes(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * 空安全的时间比较：a 是否在 b 之前
     *
     * @param a 时间a
     * @param b 时间b
     * @return 是否 a 在 b 之前
     */
    public static boolean isBefore(LocalDateTime a, LocalDateTime b) {
        if (a == null || b == null) {
            return false;
        }
        return a.isBefore(b);
    }

    /**
     * 空安全的时间比较：a 是否在 b 之后
     *
     * @param a 时间a
     * @param b 时间b
     * @return 是否 a 在 b 之后
     */
    public static boolean isAfter(LocalDateTime a, LocalDateTime b) {
        if (a == null || b == null) {
            return false;
        }
        return a.isAfter(b);
    }
}
