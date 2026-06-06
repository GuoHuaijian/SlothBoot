package com.sloth.boot.common.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Pattern;

/**
 * IP 地址工具类
 * <p>
 * 提供 IP 地址的解析、校验、转换和脱敏等功能。
 * <p>
 * 使用示例：
 * <pre>
 * // IP 与 long 互转
 * long ipLong = IpUtil.ipToLong("192.168.1.100");  // 3232235876
 * String ip = IpUtil.longToIp(3232235876L);         // "192.168.1.100"
 *
 * // IP 脱敏（用于日志打印）
 * String masked = IpUtil.maskIp("192.168.1.100");  // "192.168.*.*"
 *
 * // 判断内网 IP
 * boolean internal = IpUtil.isInternalIp("10.0.0.1"); // true
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class IpUtil {

    private IpUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

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
     * 内网 IP 前缀
     */
    private static final String IP_PREFIX_10 = "10.";
    /** 172.16.0.0 ~ 172.31.255.255（B 类内网），需额外校验第二段范围 */
    private static final String IP_PREFIX_172 = "172.";
    /** 192.168.0.0 ~ 192.168.255.255（C 类内网） */
    private static final String IP_PREFIX_192 = "192.168.";
    /** 127.0.0.0 ~ 127.255.255.255（本地回环地址） */
    private static final String IP_PREFIX_127 = "127.";

    /**
     * 未知 IP
     */
    private static final String UNKNOWN_IP = "unknown";

    /**
     * 从 HttpServletRequest 中获取客户端真实 IP
     * <p>
     * 依次检查以下请求头：
     * <ol>
     *   <li>{@code X-Real-IP}（Nginx 代理）</li>
     *   <li>{@code X-Forwarded-For}（多级代理，取第一个）</li>
     *   <li>{@code Proxy-Client-IP}（Apache 代理）</li>
     *   <li>{@code WL-Proxy-Client-IP}（WebLogic 代理）</li>
     *   <li>{@code request.getRemoteAddr()}（直接连接）</li>
     * </ol>
     *
     * @param request HttpServletRequest
     * @return 客户端 IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("X-Real-IP");
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多级代理的情况（X-Forwarded-For 可能包含多个 IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 判断是否为内网 IP
     * <p>
     * 内网 IP 范围：
     * <ul>
     *   <li>10.0.0.0 ~ 10.255.255.255（A 类）</li>
     *   <li>172.16.0.0 ~ 172.31.255.255（B 类）</li>
     *   <li>192.168.0.0 ~ 192.168.255.255（C 类）</li>
     *   <li>127.0.0.0 ~ 127.255.255.255（本地回环）</li>
     * </ul>
     *
     * @param ip IP 地址
     * @return 是否为内网 IP
     */
    public static boolean isInternalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        if (ip.startsWith(IP_PREFIX_10) || ip.startsWith(IP_PREFIX_127)) {
            return true;
        }
        if (ip.startsWith(IP_PREFIX_192)) {
            return true;
        }
        if (ip.startsWith(IP_PREFIX_172)) {
            try {
                String[] parts = ip.split("\\.");
                int second = Integer.parseInt(parts[1]);
                return second >= 16 && second <= 31;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断是否为合法 IPv4 地址
     *
     * @param ip IP 地址
     * @return 是否为 IPv4 地址
     */
    public static boolean isIpv4(String ip) {
        return ip != null && IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 判断是否为合法 IPv6 地址
     *
     * @param ip IP 地址
     * @return 是否为 IPv6 地址
     */
    public static boolean isIpv6(String ip) {
        return ip != null && IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * IPv4 地址转换为 long 值
     * <p>
     * <pre>
     * long value = IpUtil.ipToLong("192.168.1.100"); // 3232235876
     * </pre>
     *
     * @param ip IPv4 地址
     * @return long 值
     * @throws IllegalArgumentException 如果 IP 格式不合法
     */
    public static long ipToLong(String ip) {
        if (!isIpv4(ip)) {
            throw new IllegalArgumentException("非法的 IPv4 地址: " + ip);
        }
        String[] parts = ip.split("\\.");
        long result = 0;
        for (String part : parts) {
            result = result * 256 + Integer.parseInt(part);
        }
        return result;
    }

    /**
     * long 值转换为 IPv4 地址
     * <p>
     * <pre>
     * String ip = IpUtil.longToIp(3232235876L); // "192.168.1.100"
     * </pre>
     *
     * @param ipLong long 值
     * @return IPv4 地址
     */
    public static String longToIp(long ipLong) {
        return ((ipLong >> 24) & 0xFF) + "." +
            ((ipLong >> 16) & 0xFF) + "." +
            ((ipLong >> 8) & 0xFF) + "." +
            (ipLong & 0xFF);
    }

    /**
     * IP 地址脱敏（用于日志打印）
     * <p>
     * 将 IP 地址的最后一段替换为星号。
     * <pre>
     * IpUtil.maskIp("192.168.1.100") → "192.168.*.*"
     * IpUtil.maskIp("10.0.0.1")      → "10.0.*.*"
     * </pre>
     *
     * @param ip IP 地址
     * @return 脱敏后的 IP 地址
     */
    public static String maskIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return ip;
        }
        int lastDot = ip.lastIndexOf('.');
        if (lastDot > 0) {
            int secondLastDot = ip.lastIndexOf('.', lastDot - 1);
            if (secondLastDot > 0) {
                return ip.substring(0, secondLastDot) + ".*.*";
            }
        }
        return ip;
    }

    private static boolean isBlankOrUnknown(String value) {
        return value == null || value.isEmpty() || UNKNOWN_IP.equalsIgnoreCase(value);
    }
}
