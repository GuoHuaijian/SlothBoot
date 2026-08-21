package com.sloth.boot.generator.metadata;

import lombok.Builder;
import lombok.Getter;

/**
 * 数据库列定义，由 {@link DatabaseMetadataReader} 从 JDBC 元数据构建。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@Builder
public class ColumnDefinition {

    /**
     * 列名（下划线风格，如 user_name）
     */
    private final String columnName;

    /**
     * java.sql.Types 中的 JDBC 类型码
     */
    private final int jdbcTypeCode;

    /**
     * 数据库原始类型名（如 VARCHAR、BIGINT）
     */
    private final String typeName;

    /**
     * 列注释（可能为空）
     */
    private final String remark;

    /**
     * 是否允许 NULL
     */
    private final boolean nullable;

    /**
     * 字符串长度或数值精度（无长度概念的类型为 0）
     */
    private final int columnSize;

    /**
     * 小数位数
     */
    private final int decimalDigits;

    /**
     * 是否主键列
     */
    private final boolean primaryKey;

    /**
     * 是否自增列
     */
    private final boolean autoIncrement;
}
