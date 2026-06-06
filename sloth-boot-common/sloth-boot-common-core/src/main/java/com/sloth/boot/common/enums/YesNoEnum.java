package com.sloth.boot.common.enums;

/**
 * 是否枚举
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum YesNoEnum implements IBaseEnum {

    /** 是 */
    YES(1, "是"),
    /** 否 */
    NO(0, "否");

    private final int code;
    private final String desc;

    YesNoEnum(int code, String desc) {
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
