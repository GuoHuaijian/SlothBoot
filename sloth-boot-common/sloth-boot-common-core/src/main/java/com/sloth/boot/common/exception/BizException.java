package com.sloth.boot.common.exception;

/**
 * 业务异常类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class BizException extends BaseException {

    public BizException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BizException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BizException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public BizException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 创建业务异常
     *
     * @param errorCode 错误码
     * @return 业务异常
     */
    public static BizException of(ErrorCode errorCode) {
        return new BizException(errorCode);
    }

    /**
     * 创建业务异常
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @return 业务异常
     */
    public static BizException of(ErrorCode errorCode, String message) {
        return new BizException(errorCode, message);
    }

    /**
     * 创建业务异常
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 业务异常
     */
    public static BizException of(int code, String message) {
        return new BizException(new GlobalErrorCode(code, message));
    }

    /**
     * 创建业务异常
     *
     * @param message 错误信息
     * @return 业务异常
     */
    public static BizException of(String message) {
        return new BizException(GlobalErrorCode.INTERNAL_ERROR, message);
    }
}
