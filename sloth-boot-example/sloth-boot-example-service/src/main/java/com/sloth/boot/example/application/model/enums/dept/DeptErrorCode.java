package com.sloth.boot.example.application.model.enums.dept;

import com.sloth.boot.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 部门模块错误码。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DeptErrorCode implements ErrorCode {

    DEPT_NOT_FOUND(100201, "部门不存在"),
    DEPT_NAME_ALREADY_EXISTS(100202, "部门名称已存在"),
    DEPT_HAS_CHILDREN(100203, "存在子部门，无法删除");

    private final int code;
    private final String msg;
}
