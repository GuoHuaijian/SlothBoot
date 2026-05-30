package com.sloth.boot.common.exception;

/**
 * 远程调用异常基类。
 * <p>
 * 适用于 Feign、RestTemplate、WebClient 等各种远程调用场景。
 * 统一使用 {@link ErrorCode} 体系，同时携带远程服务名称和请求 URL 等上下文信息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RemoteCallException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 远程服务名称
     */
    private final String serviceName;

    /**
     * 请求 URL
     */
    private final String url;

    public RemoteCallException(String serviceName, String message) {
        super(GlobalErrorCode.INTERNAL_ERROR, message);
        this.serviceName = serviceName;
        this.url = null;
    }

    public RemoteCallException(String serviceName, ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.serviceName = serviceName;
        this.url = null;
    }

    public RemoteCallException(String serviceName, ErrorCode errorCode, String message, String url) {
        super(errorCode, message);
        this.serviceName = serviceName;
        this.url = url;
    }

    public RemoteCallException(String serviceName, ErrorCode errorCode, String message, String url, Throwable cause) {
        super(errorCode, message, cause);
        this.serviceName = serviceName;
        this.url = url;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUrl() {
        return url;
    }
}
