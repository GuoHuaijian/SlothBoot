package com.sloth.boot.common.log.annotation;

import com.sloth.boot.common.enums.IBaseEnum;

/**
 * 操作日志类型。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum OperateTypeEnum implements IBaseEnum {

    OTHER(0, "其他"),
    QUERY(1, "查询"),
    CREATE(2, "新增"),
    UPDATE(3, "修改"),
    DELETE(4, "删除"),
    IMPORT(5, "导入"),
    EXPORT(6, "导出"),
    LOGIN(7, "登录"),
    LOGOUT(8, "登出");

    private final int code;
    private final String desc;

    OperateTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
