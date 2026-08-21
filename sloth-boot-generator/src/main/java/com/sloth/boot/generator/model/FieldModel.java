package com.sloth.boot.generator.model;

import lombok.Builder;
import lombok.Getter;

/**
 * 字段视图模型，供模板渲染使用。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@Builder
public class FieldModel {

    /**
     * 列名（下划线风格）
     */
    private final String columnName;

    /**
     * 字段名（驼峰风格）
     */
    private final String fieldName;

    /**
     * Java 类型简单名（如 String、Long）
     */
    private final String javaType;

    /**
     * 字段注释（可能为空，模板中请用 $!{f.comment}）
     */
    private final String comment;

    /**
     * 展示名：有注释用注释，否则回退为字段名（用于校验消息）
     */
    private final String displayName;

    /**
     * 是否必填（数据库 NOT NULL 且非主键）
     */
    private final boolean required;

    /**
     * 字符串最大长度（非字符串类型为 0）
     */
    private final int length;

    /**
     * 是否主键
     */
    private final boolean primaryKey;

    /**
     * 是否字符串类型（决定分页查询用 like 还是 eq）
     */
    private final boolean stringType;

    /**
     * getter 方法名（如 getUsername）
     */
    private final String getterName;

    /**
     * 审计角色，用于 PO 模板选择 MyBatis-Plus 注解：
     * {@code id} / {@code insert_fill} / {@code insert_update_fill} /
     * {@code logic_delete} / {@code version}，业务字段为空串
     */
    private final String auditRole;
}
