package com.sloth.boot.example.application.model.enums.user;

import com.sloth.boot.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户模块错误码。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(100101, "用户不存在"),
    USERNAME_ALREADY_EXISTS(100102, "用户名已存在"),
    USER_DISABLED(100103, "用户已被禁用"),
    PHONE_ALREADY_EXISTS(100104, "手机号已被注册");

    private final int code;
    private final String msg;
}
