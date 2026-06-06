package com.sloth.boot.common.log.annotation;

import com.sloth.boot.common.enums.IBaseEnum;

/**
 * 操作日志类型。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum OperateTypeEnum implements IBaseEnum {

    /** 其他操作 */
    OTHER(0, "其他"),
    /** 查询操作 */
    QUERY(1, "查询"),
    /** 新增操作 */
    CREATE(2, "新增"),
    /** 修改操作 */
    UPDATE(3, "修改"),
    /** 删除操作 */
    DELETE(4, "删除"),
    /** 导入操作 */
    IMPORT(5, "导入"),
    /** 导出操作 */
    EXPORT(6, "导出"),
    /** 登录操作 */
    LOGIN(7, "登录"),
    /** 登出操作 */
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
