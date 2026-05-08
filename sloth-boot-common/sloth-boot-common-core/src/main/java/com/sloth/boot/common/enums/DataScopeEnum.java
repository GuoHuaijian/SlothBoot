package com.sloth.boot.common.enums;

/**
 * 数据权限范围枚举
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum DataScopeEnum implements IBaseEnum {

    ALL(1, "全部数据"),
    DEPT(2, "本部门数据"),
    SUB_DEPT(3, "本部门及以下数据"),
    SELF(4, "仅本人数据");

    private final int code;
    private final String desc;

    DataScopeEnum(int code, String desc) {
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
