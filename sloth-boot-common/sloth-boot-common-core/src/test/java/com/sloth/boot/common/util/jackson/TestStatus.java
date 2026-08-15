package com.sloth.boot.common.util.jackson;

import com.sloth.boot.common.enums.IBaseEnum;

/**
 * 测试用 IBaseEnum 枚举
 */
enum TestStatus implements IBaseEnum {

    INACTIVE(0, "停用"),
    ACTIVE(1, "启用");

    private final int code;
    private final String desc;

    TestStatus(int code, String desc) {
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
