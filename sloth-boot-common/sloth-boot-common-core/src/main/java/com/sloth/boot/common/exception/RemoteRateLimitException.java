package com.sloth.boot.common.exception;

/**
 * 远程服务限流异常。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RemoteRateLimitException extends RemoteCallException {

    private static final long serialVersionUID = 1L;

    public RemoteRateLimitException(String serviceName) {
        super(serviceName, GlobalErrorCode.TOO_MANY_REQUESTS, "远程服务限流: " + serviceName);
    }

    public RemoteRateLimitException(String serviceName, String url) {
        super(serviceName, GlobalErrorCode.TOO_MANY_REQUESTS, "远程服务限流: " + serviceName, url);
    }
}
