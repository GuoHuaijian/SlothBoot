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
    SUCCESS(0, "操作成功", "sloth.success"),

    /**
     * 系统内部异常
     */
    INTERNAL_ERROR(500, "系统内部异常", "sloth.error.internal"),

    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误", "sloth.error.bad_request"),

    /**
     * 未认证
     */
    UNAUTHORIZED(401, "未认证", "sloth.error.unauthorized"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限", "sloth.error.forbidden"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在", "sloth.error.not_found"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持", "sloth.error.method_not_allowed"),

    /**
     * 不支持的媒体类型
     */
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型", "sloth.error.media_type_not_supported"),

    /**
     * 请求过于频繁
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁", "sloth.error.too_many_requests"),

    /**
     * 资源冲突（乐观锁）
     */
    CONFLICT(409, "资源冲突", "sloth.error.conflict"),

    /**
     * 重复请求
     */
    REPEATED_REQUEST(900, "重复请求", "sloth.error.repeated_request"),

    /**
     * 演示模式禁止操作
     */
    DEMO_DENY(901, "演示模式禁止操作", "sloth.error.demo_deny");

    private final int code;
    private final String msg;
    private final String i18nKey;

    GlobalErrorCode(int code, String msg, String i18nKey) {
        this.code = code;
        this.msg = msg;
        this.i18nKey = i18nKey;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public String getI18nKey() {
        return this.i18nKey;
    }
}
