package com.sloth.boot.common.exception;

import java.io.Serializable;

/**
 * 远程调用异常基类
 * <p>
 * 适用于 Feign、RestTemplate、WebClient 等各种远程调用场景。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RemoteCallException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 远程服务名称
     */
    private final String serviceName;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 请求 URL
     */
    private final String url;

    public RemoteCallException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
        this.code = GlobalErrorCode.INTERNAL_ERROR.getCode();
        this.url = null;
    }

    public RemoteCallException(String serviceName, int code, String message) {
        super(message);
        this.serviceName = serviceName;
        this.code = code;
        this.url = null;
    }

    public RemoteCallException(String serviceName, int code, String message, String url) {
        super(message);
        this.serviceName = serviceName;
        this.code = code;
        this.url = url;
    }

    public RemoteCallException(String serviceName, int code, String message, String url, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.code = code;
        this.url = url;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getCode() {
        return code;
    }

    public String getUrl() {
        return url;
    }
}
