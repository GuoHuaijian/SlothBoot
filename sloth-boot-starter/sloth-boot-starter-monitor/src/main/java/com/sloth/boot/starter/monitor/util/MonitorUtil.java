package com.sloth.boot.starter.monitor.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 监控工具类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class MonitorUtil {

    private MonitorUtil() {
    }

    /**
     * 将字节数转换为 MB 字符串。
     *
     * @param bytes 字节数
     * @return 格式化的 MB 字符串
     */
    public static String bytesToMB(long bytes) {
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    /**
     * 格式化时间戳为可读字符串。
     *
     * @param millis 毫秒时间戳
     * @return 格式化的时间字符串
     */
    public static String formatTimestamp(long millis) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(millis));
    }

    /**
     * 格式化毫秒时长为可读字符串。
     *
     * @param millis 毫秒时长
     * @return 格式化的时长字符串
     */
    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        if (days > 0) {
            return String.format("%d天%d小时%d分%d秒", days, hours, minutes, secs);
        } else if (hours > 0) {
            return String.format("%d小时%d分%d秒", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%d分%d秒", minutes, secs);
        } else {
            return String.format("%d秒", secs);
        }
    }
}
