package com.sloth.boot.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * URL 操作工具类
 * <p>
 * 提供 URL 的拼接、参数操作、路径处理和编解码等功能。
 * <p>
 * 使用示例：
 * <pre>
 * // 拼接 URL 和查询参数
 * Map&lt;String, String&gt; params = new LinkedHashMap&lt;&gt;();
 * params.put("page", "1");
 * params.put("size", "10");
 * String url = UrlUtil.buildUrl("https://api.example.com/users", params);
 * // "https://api.example.com/users?page=1&amp;size=10"
 *
 * // 追加单个参数
 * String url2 = UrlUtil.appendParam("https://api.example.com/users", "page", "1");
 * // "https://api.example.com/users?page=1"
 *
 * // 路径拼接
 * String path = UrlUtil.joinPath("/api/", "/users/", "/1");
 * // "/api/users/1"
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class UrlUtil {

    private UrlUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final String PARAM_SEPARATOR = "?";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    /**
     * 拼接 URL 和查询参数
     *
     * @param baseUrl 基础 URL
     * @param params  查询参数
     * @return 完整 URL
     */
    public static String buildUrl(String baseUrl, Map<String, String> params) {
        if (baseUrl == null) {
            return null;
        }
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        String query = params.entrySet().stream()
                .map(e -> encode(e.getKey()) + KEY_VALUE_SEPARATOR + encode(e.getValue()))
                .collect(Collectors.joining(PARAM_DELIMITER));
        if (baseUrl.contains(PARAM_SEPARATOR)) {
            return baseUrl + PARAM_DELIMITER + query;
        }
        return baseUrl + PARAM_SEPARATOR + query;
    }

    /**
     * 向 URL 追加单个查询参数
     *
     * @param url   URL
     * @param name  参数名
     * @param value 参数值
     * @return 追加参数后的 URL
     */
    public static String appendParam(String url, String name, String value) {
        if (url == null) {
            return null;
        }
        String param = encode(name) + KEY_VALUE_SEPARATOR + encode(value);
        if (url.contains(PARAM_SEPARATOR)) {
            return url + PARAM_DELIMITER + param;
        }
        return url + PARAM_SEPARATOR + param;
    }

    /**
     * 向 URL 追加多个查询参数
     *
     * @param url    URL
     * @param params 查询参数
     * @return 追加参数后的 URL
     */
    public static String appendParams(String url, Map<String, String> params) {
        if (url == null) {
            return null;
        }
        if (params == null || params.isEmpty()) {
            return url;
        }
        String query = params.entrySet().stream()
                .map(e -> encode(e.getKey()) + KEY_VALUE_SEPARATOR + encode(e.getValue()))
                .collect(Collectors.joining(PARAM_DELIMITER));
        if (url.contains(PARAM_SEPARATOR)) {
            return url + PARAM_DELIMITER + query;
        }
        return url + PARAM_SEPARATOR + query;
    }

    /**
     * 移除 URL 中指定的查询参数
     *
     * @param url  URL
     * @param name 参数名
     * @return 移除参数后的 URL
     */
    public static String removeParam(String url, String name) {
        if (url == null || name == null) {
            return url;
        }
        int questionMarkIndex = url.indexOf(PARAM_SEPARATOR);
        if (questionMarkIndex < 0) {
            return url;
        }
        String baseUrl = url.substring(0, questionMarkIndex);
        String queryString = url.substring(questionMarkIndex + 1);
        String filteredQuery = java.util.Arrays.stream(queryString.split(PARAM_DELIMITER))
                .filter(param -> {
                    int eqIndex = param.indexOf(KEY_VALUE_SEPARATOR);
                    String key = eqIndex > 0 ? param.substring(0, eqIndex) : param;
                    return !name.equals(key);
                })
                .collect(Collectors.joining(PARAM_DELIMITER));
        if (filteredQuery.isEmpty()) {
            return baseUrl;
        }
        return baseUrl + PARAM_SEPARATOR + filteredQuery;
    }

    /**
     * 提取 URL 的路径部分
     *
     * @param url URL
     * @return 路径部分
     */
    public static String getPath(String url) {
        if (url == null) {
            return null;
        }
        try {
            URI uri = URI.create(url);
            return uri.getPath();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 解析 URL 查询参数为 Map
     *
     * @param url URL
     * @return 参数 Map
     */
    public static Map<String, String> getQueryParams(String url) {
        Map<String, String> params = new LinkedHashMap<>();
        if (url == null) {
            return params;
        }
        int questionMarkIndex = url.indexOf(PARAM_SEPARATOR);
        if (questionMarkIndex < 0) {
            return params;
        }
        String queryString = url.substring(questionMarkIndex + 1);
        // 去除 fragment（# 部分）
        int hashIndex = queryString.indexOf('#');
        if (hashIndex >= 0) {
            queryString = queryString.substring(0, hashIndex);
        }
        for (String param : queryString.split(PARAM_DELIMITER)) {
            int eqIndex = param.indexOf(KEY_VALUE_SEPARATOR);
            if (eqIndex > 0) {
                String key = decode(param.substring(0, eqIndex));
                String value = decode(param.substring(eqIndex + 1));
                params.put(key, value);
            }
        }
        return params;
    }

    /**
     * 获取 URL 中指定参数的值
     *
     * @param url  URL
     * @param name 参数名
     * @return 参数值，未找到返回 null
     */
    public static String getQueryParam(String url, String name) {
        return getQueryParams(url).get(name);
    }

    /**
     * 路径拼接（自动处理多余的斜杠）
     * <p>
     * <pre>
     * UrlUtil.joinPath("/api/", "/users/", "/1") → "/api/users/1"
     * UrlUtil.joinPath("api", "users", "1")      → "api/users/1"
     * </pre>
     *
     * @param parts 路径片段
     * @return 拼接后的路径
     */
    public static String joinPath(String... parts) {
        if (parts == null || parts.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] == null || parts[i].isEmpty()) {
                continue;
            }
            String part = parts[i];
            // 去除前导斜杠（第一个片段除外）
            if (sb.length() > 0 && part.startsWith("/")) {
                part = part.substring(1);
            }
            // 去除尾部斜杠
            if (i < parts.length - 1 && part.endsWith("/")) {
                part = part.substring(0, part.length() - 1);
            }
            // 添加分隔符
            if (sb.length() > 0 && !part.isEmpty()) {
                sb.append("/");
            }
            sb.append(part);
        }
        return sb.toString();
    }

    /**
     * URL 编码（UTF-8）
     *
     * @param value 待编码的字符串
     * @return 编码后的字符串
     */
    public static String encode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // UTF-8 always supported, won't happen
            return value;
        }
    }

    /**
     * URL 解码（UTF-8）
     *
     * @param value 待解码的字符串
     * @return 解码后的字符串
     */
    public static String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // UTF-8 always supported, won't happen
            return value;
        }
    }

    /**
     * 校验是否为合法 URL
     *
     * @param url URL 字符串
     * @return 是否为合法 URL
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        try {
            URI.create(url);
            return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp://");
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 提取 URL 的域名部分
     *
     * @param url URL
     * @return 域名，提取失败返回 null
     */
    public static String getDomain(String url) {
        if (url == null) {
            return null;
        }
        try {
            URI uri = URI.create(url);
            return uri.getHost();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
