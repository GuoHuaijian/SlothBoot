package com.sloth.boot.starter.excel.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入结果。
 * <p>
 * 封装导入操作的结果，包括成功数据、错误详情、处理状态等。
 *
 * @param <T> 数据类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class ExcelImportResult<T> {

    /**
     * 成功导入的数据。
     */
    private List<T> data;

    /**
     * 错误详情列表。
     */
    private List<ExcelErrorDetail> errors;

    /**
     * 成功行数。
     */
    private int successRows;

    /**
     * 失败行数。
     */
    private int failedRows;

    /**
     * 是否全部成功。
     */
    private boolean allSuccess;

    public ExcelImportResult() {
        this.data = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.allSuccess = true;
    }

    public ExcelImportResult(List<T> data, List<ExcelErrorDetail> errors) {
        this.data = data;
        this.errors = errors;
        this.successRows = data.size();
        this.failedRows = errors.size();
        this.allSuccess = errors.isEmpty();
    }

    /**
     * 添加错误详情。
     *
     * @param error 错误详情
     */
    public void addError(ExcelErrorDetail error) {
        this.errors.add(error);
        this.failedRows++;
        this.allSuccess = false;
    }

    /**
     * 递增成功行数。
     */
    public void incrementSuccess() {
        this.successRows++;
    }
}
