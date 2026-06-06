package com.sloth.boot.starter.mybatis.annotation;

import com.sloth.boot.common.enums.IBaseEnum;

/**
 * 数据权限范围枚举
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum DataScopeEnum implements IBaseEnum {

    /** 全部数据权限 */
    ALL(1, "全部数据"),
    /** 本部门数据权限 */
    DEPT(2, "本部门数据"),
    /** 本部门及以下数据权限 */
    SUB_DEPT(3, "本部门及以下数据"),
    /** 仅本人数据权限 */
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
