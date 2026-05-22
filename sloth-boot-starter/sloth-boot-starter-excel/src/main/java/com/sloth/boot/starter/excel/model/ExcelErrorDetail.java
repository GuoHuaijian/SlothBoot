package com.sloth.boot.starter.excel.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Excel 错误详情。
 * <p>
 * 记录导入过程中某一行/列的具体错误信息，包括行号、列名、原始值、错误类型和错误消息。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelErrorDetail {

    /**
     * 错误类型。
     */
    private ErrorType errorType;

    /**
     * 行号（从 1 开始）。
     */
    private int rowIndex;

    /**
     * 列名（表头名称）。
     */
    private String columnName;

    /**
     * 列索引（从 0 开始）。
     */
    private int columnIndex;

    /**
     * 原始单元格值。
     */
    private Object originalValue;

    /**
     * 错误消息。
     */
    private String message;

    /**
     * 错误类型枚举。
     */
    public enum ErrorType {
        /**
         * 类型转换错误。
         */
        TYPE_CONVERSION,
        /**
         * 数据校验错误。
         */
        VALIDATION,
        /**
         * 必填字段缺失。
         */
        REQUIRED_FIELD_MISSING,
        /**
         * 解析异常。
         */
        PARSE_EXCEPTION,
        /**
         * 业务逻辑错误。
         */
        BUSINESS,
        /**
         * 其他错误。
         */
        OTHER
    }
}
