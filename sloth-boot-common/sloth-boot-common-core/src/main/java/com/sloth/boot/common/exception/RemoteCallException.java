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

    /**
     * 构造远程调用异常（无 URL，使用默认 INTERNAL_ERROR 错误码）。
     *
     * @param serviceName 远程服务名称
     * @param message     异常消息
     */
    public RemoteCallException(String serviceName, String message) {
        super(GlobalErrorCode.INTERNAL_ERROR, message);
        this.serviceName = serviceName;
        this.url = null;
    }

    /**
     * 构造远程调用异常（无 URL，自定义错误码）。
     *
     * @param serviceName 远程服务名称
     * @param errorCode   错误码枚举
     * @param message     异常消息
     */
    public RemoteCallException(String serviceName, ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.serviceName = serviceName;
        this.url = null;
    }

    /**
     * 构造远程调用异常（带请求 URL）。
     *
     * @param serviceName 远程服务名称
     * @param errorCode   错误码枚举
     * @param message     异常消息
     * @param url         请求的 URL 地址
     */
    public RemoteCallException(String serviceName, ErrorCode errorCode, String message, String url) {
        super(errorCode, message);
        this.serviceName = serviceName;
        this.url = url;
    }

    /**
     * 构造远程调用异常（带请求 URL 和原因链）。
     *
     * @param serviceName 远程服务名称
     * @param errorCode   错误码枚举
     * @param message     异常消息
     * @param url         请求的 URL 地址
     * @param cause       原始异常
     */
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
