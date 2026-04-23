package com.sloth.boot.starter.feign.exception;

/**
 * 远程限流异常。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RateLimitException extends RemoteCallException {

    /**
     * 构造函数。
     *
     * @param message 错误信息
     */
    public RateLimitException(String message) {
        super(429, message);
    }
}
