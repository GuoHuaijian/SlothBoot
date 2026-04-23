package com.sloth.boot.common.exception;

/**
 * 错误码接口
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface ErrorCode {

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    int getCode();

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    String getMsg();
}
