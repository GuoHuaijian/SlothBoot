package com.sloth.boot.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class DateUtil {

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 格式化日期时间
     *
     * @param dateTime 日期时间
     * @param pattern  格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * 解析日期时间字符串
     *
     * @param str     日期时间字符串
     * @param pattern 格式
     * @return 日期时间
     */
    public static LocalDateTime parse(String str, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(str, formatter);
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
     * 获取当天的开始时间
     *
     * @param date 日期
     * @return 当天开始时间
     */
    public static LocalDateTime beginOfDay(LocalDateTime date) {
        return date.toLocalDate().atStartOfDay();
    }

    /**
     * 获取当天的结束时间
     *
     * @param date 日期
     * @return 当天结束时间
     */
    public static LocalDateTime endOfDay(LocalDateTime date) {
        return date.toLocalDate().atTime(23, 59, 59, 999999999);
    }

    /**
     * 获取当月的开始时间
     *
     * @param date 日期
     * @return 当月开始时间
     */
    public static LocalDateTime beginOfMonth(LocalDateTime date) {
        return date.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
    }

    /**
     * 获取当月的结束时间
     *
     * @param date 日期
     * @return 当月结束时间
     */
    public static LocalDateTime endOfMonth(LocalDateTime date) {
        return date.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(23, 59, 59, 999999999);
    }

    /**
     * 格式化日期时间为默认格式
     *
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String formatDefault(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 解析默认格式的日期时间字符串
     *
     * @param str 日期时间字符串
     * @return 日期时间
     */
    public static LocalDateTime parseDefault(String str) {
        return parse(str, DEFAULT_DATE_TIME_FORMAT);
    }
}
