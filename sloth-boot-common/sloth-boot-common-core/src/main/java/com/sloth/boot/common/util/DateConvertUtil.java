package com.sloth.boot.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 日期类型转换工具类
 * <p>
 * 提供日期时间与毫秒时间戳、{@code java.sql.Timestamp} 之间的转换方法，
 * 基于 {@code java.time} API。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class DateConvertUtil {

    private DateConvertUtil() {
        throw new UnsupportedOperationException("Utility class");
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
}
