package com.sloth.boot.starter.excel.model;

import lombok.Data;

import java.util.List;

/**
 * Sheet 数据对象。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class SheetData {

    private String sheetName;
    private Class<?> head;
    private List<?> data;
}
