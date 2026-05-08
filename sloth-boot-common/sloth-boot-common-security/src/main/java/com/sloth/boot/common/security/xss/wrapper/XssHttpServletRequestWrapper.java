package com.sloth.boot.common.security.xss.wrapper;

import com.sloth.boot.common.security.xss.XssCleaner;
import com.sloth.boot.common.security.xss.XssProperties;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * XSS 请求包装器。
 * <p>
 * 对 {@link HttpServletRequest} 进行包装，自动对请求参数（query parameter）进行 XSS 清洗。
 * 同时缓存请求体的字节数组，支持对 body 内容的二次读取。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final XssProperties xssProperties;
    private byte[] body;

    /**
     * 构造 XSS 请求包装器（无原始请求，用于测试场景）。
     *
     * @param xssProperties XSS 配置
     */
    public XssHttpServletRequestWrapper(XssProperties xssProperties) {
        super(null);
        this.xssProperties = xssProperties;
    }

    /**
     * 构造 XSS 请求包装器。
     *
     * @param request       原始 HTTP 请求
     * @param xssProperties XSS 配置
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request, XssProperties xssProperties) {
        super(request);
        this.xssProperties = xssProperties;
        try {
            body = request.getInputStream().readAllBytes();
        } catch (IOException e) {
            body = new byte[0];
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
                // no-op
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
        if (value != null) {
            value = XssCleaner.clean(value, xssProperties);
        }
        return value;
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
}
