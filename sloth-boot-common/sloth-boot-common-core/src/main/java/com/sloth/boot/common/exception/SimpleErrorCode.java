package com.sloth.boot.common.exception;

/**
 * 简单的不可变错误码实现，用于动态错误场景。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public record SimpleErrorCode(int code, String msg) implements ErrorCode {

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
