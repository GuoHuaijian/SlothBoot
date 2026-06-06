package com.sloth.boot.common.exception;

/**
 * 远程服务未找到异常。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RemoteServiceNotFoundException extends RemoteCallException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造远程服务未找到异常。
     *
     * @param serviceName 远程服务名称
     */
    public RemoteServiceNotFoundException(String serviceName) {
        super(serviceName, GlobalErrorCode.NOT_FOUND, "远程服务未找到: " + serviceName);
    }

    /**
     * 构造远程服务未找到异常（含请求 URL）。
     *
     * @param serviceName 远程服务名称
     * @param url         请求 URL
     */
    public RemoteServiceNotFoundException(String serviceName, String url) {
        super(serviceName, GlobalErrorCode.NOT_FOUND, "远程服务未找到: " + serviceName, url);
    }
}
