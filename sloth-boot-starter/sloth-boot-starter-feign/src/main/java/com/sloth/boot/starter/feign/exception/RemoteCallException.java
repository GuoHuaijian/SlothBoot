package com.sloth.boot.starter.feign.exception;

/**
 * 远程调用异常。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RemoteCallException extends RuntimeException {

    private final int code;

    /**
     * 构造函数。
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public RemoteCallException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码。
     *
     * @return 错误码
     */
    public int getCode() {
        return code;
    }
}
