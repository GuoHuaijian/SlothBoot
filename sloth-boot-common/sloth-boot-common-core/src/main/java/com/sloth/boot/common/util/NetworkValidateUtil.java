package com.sloth.boot.common.util;

import java.util.regex.Pattern;

/**
 * 网络与技术格式验证工具类
 * <p>
 * 提供网络地址和技术格式的校验方法，包括 URL、IP 地址、域名、端口号、MAC 地址等。
 * 所有方法对 null 输入均返回 {@code false}。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class NetworkValidateUtil {

    private NetworkValidateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * URL正则表达式
     */
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

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
     * MAC 地址正则表达式
     */
    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile(
        "^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$");

    /**
     * 域名正则表达式
     */
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
        "^(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.[A-Za-z0-9-]{1,63})*(\\.[A-Za-z]{2,})$");

    /**
     * 数字正则表达式
     */
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");

    /**
     * 判断是否为 URL
     *
     * @param url URL
     * @return 是否为 URL
     */
    public static boolean isUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    /**
     * 判断是否为 IPv4 地址
     *
     * @param ip IP 地址
     * @return 是否为 IPv4 地址
     */
    public static boolean isIpv4(String ip) {
        return ip != null && IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 判断是否为 IPv6 地址
     *
     * @param ip IP 地址
     * @return 是否为 IPv6 地址
     */
    public static boolean isIpv6(String ip) {
        return ip != null && IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * 判断是否为 IP 地址（IPv4 或 IPv6）
     *
     * @param ip IP 地址
     * @return 是否为 IP 地址
     */
    public static boolean isIp(String ip) {
        return isIpv4(ip) || isIpv6(ip);
    }

    /**
     * 判断是否为合法域名
     *
     * @param domain 域名
     * @return 是否为合法域名
     */
    public static boolean isDomain(String domain) {
        return domain != null && DOMAIN_PATTERN.matcher(domain).matches();
    }

    /**
     * 判断是否为合法端口号（1-65535）
     *
     * @param port 端口号字符串
     * @return 是否为合法端口号
     */
    public static boolean isPort(String port) {
        if (port == null || port.isEmpty()) {
            return false;
        }
        if (!NUMERIC_PATTERN.matcher(port).matches()) {
            return false;
        }
        try {
            int portNum = Integer.parseInt(port);
            return portNum >= 1 && portNum <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断是否为 MAC 地址
     *
     * @param mac MAC 地址
     * @return 是否为 MAC 地址
     */
    public static boolean isMacAddress(String mac) {
        return mac != null && MAC_ADDRESS_PATTERN.matcher(mac).matches();
    }
}
