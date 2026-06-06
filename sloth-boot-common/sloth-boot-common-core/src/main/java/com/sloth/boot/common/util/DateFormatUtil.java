package com.sloth.boot.common.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期格式化工具类
 * <p>
 * 提供日期时间的格式化与解析方法，基于 {@code java.time} API。
 * 内部维护 {@link DateTimeFormatter} 缓存以提升性能。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class DateFormatUtil {

    private DateFormatUtil() {
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
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

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
     * 解析默认格式的日期时间字符串
     *
     * @param str 日期时间字符串
     * @return 日期时间
     */
    public static LocalDateTime parseDefault(String str) {
        return parse(str, DEFAULT_DATE_TIME_FORMAT);
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
     * 解析默认格式的日期字符串
     *
     * @param str 日期字符串
     * @return 日期
     */
    public static LocalDate parseDateDefault(String str) {
        return parseDate(str, DEFAULT_DATE_FORMAT);
    }

    /**
     * 格式化 Duration 为人类可读格式
     * <p>
     * <pre>
     * Duration duration = Duration.ofHours(2).plusMinutes(30).plusSeconds(15);
     * String formatted = DateFormatUtil.formatDuration(duration); // "2h 30m 15s"
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
