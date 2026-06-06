package com.sloth.boot.common.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期边界工具类
 * <p>
 * 提供日期时间的边界提取方法（如当天、当月、当年、本周的开始和结束时间），
 * 基于 {@code java.time} API。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class DateBoundaryUtil {

    private DateBoundaryUtil() {
        throw new UnsupportedOperationException("Utility class");
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
}
