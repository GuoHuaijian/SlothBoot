package com.sloth.boot.common.exception;

import lombok.Getter;

/**
 * 全局错误码枚举
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public enum GlobalErrorCode implements ErrorCode {

    /**
     * 操作成功
     */
    SUCCESS(0, "操作成功"),

    /**
     * 系统内部异常
     */
    INTERNAL_ERROR(500, "系统内部异常"),

    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * 未认证
     */
    UNAUTHORIZED(401, "未认证"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求过于频繁
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    /**
     * 重复请求
     */
    REPEATED_REQUEST(900, "重复请求"),

    /**
     * 演示模式禁止操作
     */
    DEMO_DENY(901, "演示模式禁止操作");

    private final int code;
    private final String msg;

    GlobalErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
