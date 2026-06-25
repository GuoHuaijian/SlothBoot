package com.sloth.boot.example.application.model.enums.auth;

import com.sloth.boot.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证模块错误码。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    USER_NOT_FOUND(100501, "用户不存在"),
    NOT_LOGGED_IN(100502, "未登录或登录已过期");

    private final int code;
    private final String msg;
}
