package com.sloth.boot.starter.feign.exception;

/**
 * 服务不存在异常。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class ServiceNotFoundException extends RemoteCallException {

    /**
     * 构造函数。
     *
     * @param message 错误信息
     */
    public ServiceNotFoundException(String message) {
        super(404, message);
    }
}
