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
 * <p>
 * 请求体缓存策略：仅对文本类内容（JSON/XML 等）缓存请求体以支持二次读取；
 * multipart 上传、二进制流等大体积内容直接透传原始流，避免整包读入内存。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);

    /**
     * 二进制流内容类型前缀
     */
    private static final String MULTIPART_PREFIX = "multipart/";

    /**
     * 二进制流内容类型
     */
    private static final String OCTET_STREAM = "application/octet-stream";

    private final XssProperties xssProperties;

    /**
     * 缓存的请求体字节数组；不缓存时为 {@code null}
     */
    private final byte[] body;

    /**
     * 构造 XSS 请求包装器。
     * <p>
     * 文本类请求体（无 Content-Type、JSON、XML、表单等）会被缓存以支持二次读取；
     * multipart 与二进制流不缓存，保持单次读取语义。
     *
     * @param request       原始 HTTP 请求
     * @param xssProperties XSS 配置
     * @throws UncheckedIOException body 读取失败时快速失败
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request, XssProperties xssProperties) {
        super(request);
        this.xssProperties = xssProperties;
        if (shouldCacheBody(request)) {
            try {
                body = request.getInputStream().readAllBytes();
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read request body for XSS wrapping", e);
            }
        } else {
            body = null;
        }
    }

    /**
     * 判断是否需要缓存请求体：multipart 与二进制流体积可能很大，直接透传。
     *
     * @param request 原始 HTTP 请求
     * @return 是否缓存
     */
    private boolean shouldCacheBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return true;
        }
        String normalized = contentType.toLowerCase(java.util.Locale.ROOT);
        return !normalized.startsWith(MULTIPART_PREFIX) && !normalized.startsWith(OCTET_STREAM);
    }

    /**
     * 获取输入流。已缓存时基于缓存的字节数组支持多次读取；未缓存时透传原始输入流。
     *
     * @return Servlet 输入流
     * @throws IOException IO 异常
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (body == null) {
            return super.getInputStream();
        }
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
        if (values == null) {
            return null;
        }
        // 拷贝后再清洗，避免修改容器内部缓存的参数数组
        String[] cleanedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanedValues[i] = XssCleaner.clean(values[i], xssProperties);
        }
        return cleanedValues;
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
