package com.sloth.boot.common.exception;

/**
 * Simple immutable error code implementation for dynamic errors.
 *
 * @author sloth-boot
 * @since 1.0.0
 */
record SimpleErrorCode(int code, String msg) implements ErrorCode {

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
