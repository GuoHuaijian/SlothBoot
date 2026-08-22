package com.sloth.boot.starter.web.util;

import com.sloth.boot.common.exception.SystemException;
import com.sloth.boot.common.util.JsonUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet 工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ServletUtil {

    private ServletUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取 HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    /**
     * 获取 HttpServletResponse
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getResponse();
    }

    /**
     * 未知 IP 标识
     */
    private static final String UNKNOWN_IP = "unknown";

    /**
     * 获取客户端 IP 地址。
     * <p>
     * 依次检查 {@code X-Real-IP}、{@code X-Forwarded-For}（多级代理取第一个）、
     * {@code Proxy-Client-IP}、{@code WL-Proxy-Client-IP}，均缺失时回退
     * {@code request.getRemoteAddr()}。请求头可被伪造，生产环境应确保只在可信代理后使用。
     *
     * @param request HttpServletRequest，可为 null（非 Web 上下文）
     * @return 客户端 IP 地址，无法获取时返回 null
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = firstNonBlankHeader(request, "X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 按优先级返回第一个非空且非 unknown 的请求头。
     *
     * @param request HttpServletRequest
     * @param names   请求头名称（按优先级排列）
     * @return 第一个有效值，均无效时返回 null
     */
    private static String firstNonBlankHeader(HttpServletRequest request, String... names) {
        for (String name : names) {
            String value = request.getHeader(name);
            if (value != null && !value.isEmpty() && !UNKNOWN_IP.equalsIgnoreCase(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取 User-Agent
     *
     * @param request HttpServletRequest
     * @return User-Agent
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 判断是否为 AJAX 请求
     *
     * @param request HttpServletRequest
     * @return 是否为 AJAX 请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }

    /**
     * 获取请求头
     *
     * @param name 请求头名称
     * @return 请求头值
     */
    public static String getHeader(String name) {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getHeader(name);
    }

    /**
     * 获取所有请求头
     *
     * @return 请求头 Map
     */
    public static Map<String, String> getAllHeaders() {
        HttpServletRequest request = getRequest();
        Map<String, String> headers = new HashMap<>();
        if (request == null) {
            return headers;
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    /**
     * 获取请求参数值
     *
     * @param name 参数名
     * @return 参数值
     */
    public static String getRequestParam(String name) {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getParameter(name);
    }

    /**
     * 获取请求参数值（带默认值）
     *
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static String getRequestParam(String name, String defaultValue) {
        String value = getRequestParam(name);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取所有请求参数
     *
     * @return 参数 Map
     */
    public static Map<String, String[]> getAllRequestParams() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return new HashMap<>(2);
        }
        return new HashMap<>(request.getParameterMap());
    }

    /**
     * 获取 Cookie 值
     *
     * @param name Cookie 名称
     * @return Cookie 值
     */
    public static String getCookie(String name) {
        return getCookie(getRequest(), name);
    }

    /**
     * 获取 Cookie 值
     *
     * @param request HttpServletRequest
     * @param name    Cookie 名称
     * @return Cookie 值
     */
    public static String getCookie(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 获取请求 Content-Type
     *
     * @return Content-Type
     */
    public static String getContentType() {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getContentType();
    }

    /**
     * 获取请求 URI
     *
     * @return 请求 URI
     */
    public static String getRequestUri() {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getRequestURI();
    }

    /**
     * 获取请求 HTTP 方法
     *
     * @return HTTP 方法（GET/POST/PUT/DELETE 等）
     */
    public static String getRequestMethod() {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getMethod();
    }

    /**
     * 从 Authorization 请求头获取 Bearer Token
     *
     * @return Bearer Token，未找到返回 null
     */
    public static String getBearerToken() {
        String authorization = getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    /**
     * 向响应写入 JSON（UTF-8, application/json）
     *
     * @param response HttpServletResponse
     * @param data     数据对象
     */
    public static void writeJson(HttpServletResponse response, Object data) {
        writeJson(response, HttpServletResponse.SC_OK, data);
    }

    /**
     * 向响应写入 JSON（UTF-8, application/json）
     *
     * @param response HttpServletResponse
     * @param status   HTTP 状态码
     * @param data     数据对象
     */
    public static void writeJson(HttpServletResponse response, int status, Object data) {
        if (response == null) {
            return;
        }
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write(JsonUtil.toJson(data));
            response.getWriter().flush();
        } catch (IOException e) {
            throw SystemException.of("Write JSON response failed", e);
        }
    }
}
