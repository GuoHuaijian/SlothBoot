package com.sloth.boot.starter.excel.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Excel 错误详情")
public class ExcelErrorDetail {

    /**
     * 错误类型。
     */
    @Schema(description = "错误类型")
    private ErrorType errorType;

    /**
     * 行号（从 1 开始）。
     */
    @Schema(description = "行号（从1开始）", example = "5")
    private int rowIndex;

    /**
     * 列名（表头名称）。
     */
    @Schema(description = "列名（表头名称）", example = "用户名")
    private String columnName;

    /**
     * 列索引（从 0 开始）。
     */
    @Schema(description = "列索引（从0开始）", example = "2")
    private int columnIndex;

    /**
     * 原始单元格值。
     */
    @Schema(description = "原始单元格值")
    private Object originalValue;

    /**
     * 错误消息。
     */
    @Schema(description = "错误消息", example = "手机号格式不正确")
    private String message;

    /**
     * 错误类型枚举。
     */
    @Schema(description = "错误类型枚举")
    public enum ErrorType {
        /**
         * 类型转换错误。
         */
        @Schema(description = "类型转换错误")
        TYPE_CONVERSION,
        /**
         * 数据校验错误。
         */
        @Schema(description = "数据校验错误")
        VALIDATION,
        /**
         * 必填字段缺失。
         */
        @Schema(description = "必填字段缺失")
        REQUIRED_FIELD_MISSING,
        /**
         * 解析异常。
         */
        @Schema(description = "解析异常")
        PARSE_EXCEPTION,
        /**
         * 业务逻辑错误。
         */
        @Schema(description = "业务逻辑错误")
        BUSINESS,
        /**
         * 其他错误。
         */
        @Schema(description = "其他错误")
        OTHER
    }
}
