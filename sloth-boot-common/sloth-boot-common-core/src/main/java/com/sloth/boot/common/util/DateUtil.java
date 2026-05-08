package com.sloth.boot.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期工具类
 * <p>
 * 提供常用的日期时间操作方法，基于 {@code java.time} API。
 * 所有方法均为 null 安全或在参数为 null 时抛出异常。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class DateUtil {

    private DateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

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
     * 缓存的常用格式化器
     */
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new HashMap<>();

    static {
        FORMATTER_CACHE.put(DEFAULT_DATE_TIME_FORMAT, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
        FORMATTER_CACHE.put(DEFAULT_DATE_FORMAT, DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
        FORMATTER_CACHE.put(DEFAULT_TIME_FORMAT, DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT));
    }

    /**
     * 获取缓存的 DateTimeFormatter
     *
     * @param pattern 格式模式
     * @return DateTimeFormatter
     */
    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static LocalDate nowDate() {
        return LocalDate.now();
    }

    /**
     * 格式化日期时间
     *
     * @param dateTime 日期时间
     * @param pattern  格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(getFormatter(pattern));
    }

    /**
     * 格式化日期
     *
     * @param date    日期
     * @param pattern 格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date, String pattern) {
        return date.format(getFormatter(pattern));
    }

    /**
     * 解析日期时间字符串
     *
     * @param str     日期时间字符串
     * @param pattern 格式
     * @return 日期时间
     */
    public static LocalDateTime parse(String str, String pattern) {
        return LocalDateTime.parse(str, getFormatter(pattern));
    }

    /**
     * 解析日期字符串
     *
     * @param str     日期字符串
     * @param pattern 格式
     * @return 日期
     */
    public static LocalDate parseDate(String str, String pattern) {
        return LocalDate.parse(str, getFormatter(pattern));
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
     * 格式化日期为默认格式
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatDefault(LocalDate date) {
        return format(date, DEFAULT_DATE_FORMAT);
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

    /**
     * 解析默认格式的日期字符串
     *
     * @param str 日期字符串
     * @return 日期
     */
    public static LocalDate parseDateDefault(String str) {
        return parseDate(str, DEFAULT_DATE_FORMAT);
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
     * 转换为毫秒时间戳（基于系统默认时区）
     *
     * @param dateTime 日期时间
     * @return 毫秒时间戳
     */
    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 从毫秒时间戳转换为 LocalDateTime
     *
     * @param epochMilli 毫秒时间戳
     * @return 日期时间
     */
    public static LocalDateTime ofEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }

    /**
     * 转换为 java.sql.Timestamp
     *
     * @param dateTime 日期时间
     * @return Timestamp 对象
     */
    public static java.sql.Timestamp toTimestamp(LocalDateTime dateTime) {
        return java.sql.Timestamp.valueOf(dateTime);
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
     * 获取当年的开始时间
     *
     * @param date 日期
     * @return 当年开始时间
     */
    public static LocalDateTime beginOfYear(LocalDateTime date) {
        return LocalDateTime.of(date.getYear(), 1, 1, 0, 0, 0);
    }

    /**
     * 获取当年的结束时间
     *
     * @param date 日期
     * @return 当年结束时间
     */
    public static LocalDateTime endOfYear(LocalDateTime date) {
        return LocalDateTime.of(date.getYear(), 12, 31, 23, 59, 59, 999999999);
    }

    /**
     * 获取本周的开始时间（周一 00:00:00）
     *
     * @param date 日期
     * @return 本周开始时间
     */
    public static LocalDateTime beginOfWeek(LocalDateTime date) {
        return date.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
    }

    /**
     * 获取本周的结束时间（周日 23:59:59）
     *
     * @param date 日期
     * @return 本周结束时间
     */
    public static LocalDateTime endOfWeek(LocalDateTime date) {
        return date.with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59, 999999999);
    }

    /**
     * 根据生日计算年龄
     * <p>
     * <pre>
     * LocalDate birthday = LocalDate.of(1990, 6, 15);
     * int age = DateUtil.getAge(birthday); // 以当前日期为参考
     * </pre>
     *
     * @param birthday 生日
     * @return 年龄
     */
    public static int getAge(LocalDate birthday) {
        return getAge(birthday, LocalDate.now());
    }

    /**
     * 根据生日和参考日期计算年龄
     * <p>
     * <pre>
     * LocalDate birthday = LocalDate.of(1990, 6, 15);
     * LocalDate reference = LocalDate.of(2024, 1, 1);
     * int age = DateUtil.getAge(birthday, reference); // 33
     * </pre>
     *
     * @param birthday  生日
     * @param reference 参考日期
     * @return 年龄
     */
    public static int getAge(LocalDate birthday, LocalDate reference) {
        return Period.between(birthday, reference).getYears();
    }

    /**
     * 判断是否为周末（周六或周日）
     *
     * @param dateTime 日期时间
     * @return 是否为周末
     */
    public static boolean isWeekend(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    /**
     * 判断是否为工作日（周一到周五）
     *
     * @param dateTime 日期时间
     * @return 是否为工作日
     */
    public static boolean isWorkday(LocalDateTime dateTime) {
        return !isWeekend(dateTime);
    }

    /**
     * 获取星期几（1=周一，7=周日）
     *
     * @param dateTime 日期时间
     * @return 星期几
     */
    public static int dayOfWeek(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek().getValue();
    }

    /**
     * 格式化 Duration 为人类可读格式
     * <p>
     * <pre>
     * Duration duration = Duration.ofHours(2).plusMinutes(30).plusSeconds(15);
     * String formatted = DateUtil.formatDuration(duration); // "2h 30m 15s"
     * </pre>
     *
     * @param duration 时间段
     * @return 格式化后的字符串（如 "2h 30m 15s"）
     */
    public static String formatDuration(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) {
            sb.append(minutes).append("m ");
        }
        sb.append(seconds).append("s");
        return sb.toString();
    }
}
