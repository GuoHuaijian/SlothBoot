package com.sloth.boot.generator.metadata;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * 数据库表定义，由 {@link DatabaseMetadataReader} 从 JDBC 元数据构建。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@Builder
public class TableDefinition {

    /**
     * 表名（如 sys_user）
     */
    private final String tableName;

    /**
     * 表注释（可能为空）
     */
    private final String remark;

    /**
     * 全部列（按声明顺序）
     */
    private final List<ColumnDefinition> columns;

    /**
     * 主键列名（无主键为空）
     */
    private final String primaryKeyColumn;

    /**
     * 唯一索引列名列表（不含主键，用于生成"已存在"错误码）
     */
    private final List<String> uniqueColumns;
}
