package com.sloth.boot.common.enums;

/**
 * 删除标记枚举
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum DeletedEnum implements IBaseEnum {

    /** 正常（未删除） */
    NORMAL(0, "正常"),
    /** 已删除 */
    DELETED(1, "已删除");

    private final int code;
    private final String desc;

    DeletedEnum(int code, String desc) {
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
