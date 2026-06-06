package com.sloth.boot.common.exception;

import lombok.Getter;

/**
 * 基础异常类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 构造基础异常。
     *
     * @param errorCode 错误码枚举，异常消息取自 {@link ErrorCode#getMsg()}
     */
    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    /**
     * 构造基础异常（自定义消息）。
     *
     * @param errorCode 错误码枚举
     * @param message   自定义异常消息，覆盖错误码的默认消息
     */
    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造基础异常（带原因链）。
     *
     * @param errorCode 错误码枚举，异常消息取自 {@link ErrorCode#getMsg()}
     * @param cause     原始异常
     */
    public BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMsg(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造基础异常（自定义消息 + 原因链）。
     *
     * @param errorCode 错误码枚举
     * @param message   自定义异常消息
     * @param cause     原始异常
     */
    public BaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public int getCode() {
        return errorCode.getCode();
    }
}
