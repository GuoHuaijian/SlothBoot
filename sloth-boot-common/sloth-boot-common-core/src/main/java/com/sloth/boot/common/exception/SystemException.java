package com.sloth.boot.common.exception;

/**
 * 系统异常类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class SystemException extends BaseException {

    public SystemException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SystemException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public SystemException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public SystemException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 创建系统异常
     *
     * @param errorCode 错误码
     * @return 系统异常
     */
    public static SystemException of(ErrorCode errorCode) {
        return new SystemException(errorCode);
    }

    /**
     * 创建系统异常
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @return 系统异常
     */
    public static SystemException of(ErrorCode errorCode, String message) {
        return new SystemException(errorCode, message);
    }

    /**
     * 创建系统异常
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 系统异常
     */
    public static SystemException of(int code, String message) {
        return new SystemException(new GlobalErrorCode(code, message));
    }

    /**
     * 创建系统异常
     *
     * @param message 错误信息
     * @return 系统异常
     */
    public static SystemException of(String message) {
        return new SystemException(GlobalErrorCode.INTERNAL_ERROR, message);
    }
}
