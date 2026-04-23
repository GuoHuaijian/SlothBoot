package com.sloth.boot.common.security.xss.wrapper;

import com.sloth.boot.common.security.xss.XssProperties;
import com.sloth.boot.common.security.xss.XssCleaner;
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
 * XSS 请求包装器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final XssProperties xssProperties;
    private byte[] body;

    public XssHttpServletRequestWrapper(XssProperties xssProperties) {
        super(null);
        this.xssProperties = xssProperties;
    }

    public XssHttpServletRequestWrapper(HttpServletRequest request, XssProperties xssProperties) {
        super(request);
        this.xssProperties = xssProperties;
        try {
            body = request.getInputStream().readAllBytes();
        } catch (IOException e) {
            body = new byte[0];
        }
    }

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

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value != null) {
            value = XssCleaner.clean(value, xssProperties);
        }
        return value;
    }

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
