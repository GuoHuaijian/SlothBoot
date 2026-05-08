package com.sloth.boot.common.enums;

/**
 * 性别枚举
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum GenderEnum implements IBaseEnum {

    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    private final int code;
    private final String desc;

    GenderEnum(int code, String desc) {
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
