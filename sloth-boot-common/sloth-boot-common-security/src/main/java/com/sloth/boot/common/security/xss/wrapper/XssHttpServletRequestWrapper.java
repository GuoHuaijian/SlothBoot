package com.sloth.boot.common.security.xss.wrapper;

import com.sloth.boot.common.security.xss.XssCleaner;
import com.sloth.boot.common.security.xss.XssProperties;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS 请求包装器。
 * <p>
 * 对 {@link HttpServletRequest} 进行包装，自动对请求参数（query parameter）、
 * {@code getParameterMap}、{@code getQueryString} 进行 XSS 清洗。
 * 同时缓存请求体的字节数组，支持对 body 内容的二次读取。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);

    private final XssProperties xssProperties;
    private final byte[] body;

    /**
     * 构造 XSS 请求包装器。
     *
     * @param request       原始 HTTP 请求
     * @param xssProperties XSS 配置
     * @throws UncheckedIOException body 读取失败时快速失败
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request, XssProperties xssProperties) {
        super(request);
        this.xssProperties = xssProperties;
        try {
            body = request.getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read request body for XSS wrapping", e);
        }
    }

    /**
     * 获取输入流，基于缓存的请求体字节数组，支持多次读取。
     *
     * @return Servlet 输入流
     * @throws IOException IO 异常
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                if (readListener == null) {
                    return;
                }
                // 请求体已完全缓冲在内存中，异步读取退化为同步排空后通知完成
                Thread.ofVirtual().name("xss-read-listener").start(() -> {
                    try {
                        while (bais.read() != -1) {
                            // drain buffered body
                        }
                        readListener.onAllDataRead();
                    } catch (IOException e) {
                        readListener.onError(e);
                    }
                });
            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    /**
     * 获取字符读取器。
     *
     * @return BufferedReader
     * @throws IOException IO 异常
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 获取单个请求参数，自动进行 XSS 清洗。
     *
     * @param name 参数名
     * @return XSS 清洗后的参数值
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return value != null ? XssCleaner.clean(value, xssProperties) : null;
    }

    /**
     * 获取参数值数组，自动对每个值进行 XSS 清洗。
     *
     * @param name 参数名
     * @return XSS 清洗后的参数值数组
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                values[i] = XssCleaner.clean(values[i], xssProperties);
            }
        }
        return values;
    }

    /**
     * 获取全部参数 Map，自动对每个值进行 XSS 清洗，避免绕过 {@code getParameter} 清洗。
     *
     * @return XSS 清洗后的参数 Map
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> rawParameters = super.getParameterMap();
        if (rawParameters == null || rawParameters.isEmpty()) {
            return rawParameters;
        }
        Map<String, String[]> cleanedParameters = new HashMap<>(rawParameters.size());
        rawParameters.forEach((name, values) -> {
            if (values == null) {
                cleanedParameters.put(name, null);
                return;
            }
            String[] cleanedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleanedValues[i] = XssCleaner.clean(values[i], xssProperties);
            }
            cleanedParameters.put(name, cleanedValues);
        });
        return Collections.unmodifiableMap(cleanedParameters);
    }

    /**
     * 获取原始查询字符串，自动进行 XSS 清洗。
     *
     * @return XSS 清洗后的查询字符串
     */
    @Override
    public String getQueryString() {
        String queryString = super.getQueryString();
        return queryString != null ? XssCleaner.clean(queryString, xssProperties) : null;
    }
}
