package com.sloth.boot.starter.excel.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Sheet 数据对象。
 * <p>
 * 用于多 Sheet 导出时描述每个 Sheet 的名称、表头和数据。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
public class SheetData {

    /**
     * Sheet 名称。
     */
    private String sheetName;

    /**
     * 表头类型。
     */
    private Class<?> head;

    /**
     * 数据列表。
     */
    private List<?> data;

    public SheetData(String sheetName, Class<?> head, List<?> data) {
        this.sheetName = sheetName;
        this.head = head;
        this.data = data;
    }
}
