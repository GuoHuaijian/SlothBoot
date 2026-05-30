package com.sloth.boot.common.exception;

/**
 * 远程服务未找到异常。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RemoteServiceNotFoundException extends RemoteCallException {

    private static final long serialVersionUID = 1L;

    public RemoteServiceNotFoundException(String serviceName) {
        super(serviceName, GlobalErrorCode.NOT_FOUND, "远程服务未找到: " + serviceName);
    }

    public RemoteServiceNotFoundException(String serviceName, String url) {
        super(serviceName, GlobalErrorCode.NOT_FOUND, "远程服务未找到: " + serviceName, url);
    }
}
