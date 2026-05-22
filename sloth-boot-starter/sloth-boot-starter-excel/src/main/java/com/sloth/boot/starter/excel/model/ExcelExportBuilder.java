package com.sloth.boot.starter.excel.model;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.Getter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Excel 导出构建器。
 * <p>
 * 提供流式 API 构建自定义导出，支持样式、列自适应、合并单元格等。
 *
 * @param <T> 数据类型
 * @author sloth-boot
 * @since 1.0.0
 */
public class ExcelExportBuilder<T> {

    @Getter
    private final String fileName;
    @Getter
    private final Class<T> head;
    private final List<T> data;
    private String sheetName = "Sheet1";
    private boolean autoSizeColumn = true;
    private final List<WriteHandler> writeHandlers = new ArrayList<>();

    // 样式配置
    private String headerBackgroundColor;
    private String headerFontColor;
    private Boolean headerFontBold;
    private Short headerFontSize;

    public List<T> getData() {
        return Collections.unmodifiableList(data);
    }

    /**
     * 构造函数。
     *
     * @param fileName 文件名（不含后缀）
     * @param head     表头类型
     * @param data     数据
     */
    public ExcelExportBuilder(String fileName, Class<T> head, List<T> data) {
        this.fileName = fileName;
        this.head = head;
        this.data = data;
    }

    /**
     * 设置 Sheet 名称。
     *
     * @param sheetName Sheet 名称
     * @return this
     */
    public ExcelExportBuilder<T> sheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    /**
     * 设置是否自动调整列宽。
     *
     * @param autoSizeColumn 是否自动调整列宽
     * @return this
     */
    public ExcelExportBuilder<T> autoSizeColumn(boolean autoSizeColumn) {
        this.autoSizeColumn = autoSizeColumn;
        return this;
    }

    /**
     * 设置表头背景色。
     *
     * @param headerBackgroundColor 十六进制颜色（如 "#F2F2F2"）
     * @return this
     */
    public ExcelExportBuilder<T> headerBackgroundColor(String headerBackgroundColor) {
        this.headerBackgroundColor = headerBackgroundColor;
        return this;
    }

    /**
     * 设置表头字体颜色。
     *
     * @param headerFontColor 十六进制颜色（如 "#000000"）
     * @return this
     */
    public ExcelExportBuilder<T> headerFontColor(String headerFontColor) {
        this.headerFontColor = headerFontColor;
        return this;
    }

    /**
     * 设置表头字体是否加粗。
     *
     * @param headerFontBold 是否加粗
     * @return this
     */
    public ExcelExportBuilder<T> headerFontBold(Boolean headerFontBold) {
        this.headerFontBold = headerFontBold;
        return this;
    }

    /**
     * 设置表头字体大小。
     *
     * @param headerFontSize 字体大小
     * @return this
     */
    public ExcelExportBuilder<T> headerFontSize(Short headerFontSize) {
        this.headerFontSize = headerFontSize;
        return this;
    }

    /**
     * 添加自定义写入处理器。
     *
     * @param handler 写入处理器
     * @return this
     */
    public ExcelExportBuilder<T> addWriteHandler(WriteHandler handler) {
        this.writeHandlers.add(handler);
        return this;
    }

    /**
     * 写入到输出流。
     *
     * @param outputStream 输出流
     * @throws IOException IO 异常
     */
    public void write(OutputStream outputStream) throws IOException {
        ExcelWriterBuilder writerBuilder = EasyExcel.write(outputStream, head);

        // 设置 Sheet 名称
        writerBuilder.sheet(sheetName);

        // 应用样式策略
        if (hasStyleConfig()) {
            writerBuilder.registerWriteHandler(buildStyleStrategy());
        }

        // 注册自定义处理器
        for (WriteHandler handler : writeHandlers) {
            writerBuilder.registerWriteHandler(handler);
        }

        // 自动列宽
        if (autoSizeColumn) {
            writerBuilder.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
        }

        // 写入数据
        WriteSheet writeSheet = EasyExcel.writerSheet(0, sheetName).head(head).build();
        try (ExcelWriter excelWriter = writerBuilder.build()) {
            excelWriter.write(data, writeSheet);
        }
    }

    /**
     * 是否有自定义样式配置。
     *
     * @return 是否有样式配置
     */
    private boolean hasStyleConfig() {
        return headerBackgroundColor != null || headerFontColor != null
            || headerFontBold != null || headerFontSize != null;
    }

    /**
     * 构建表头样式策略。
     *
     * @return 样式策略
     */
    private HorizontalCellStyleStrategy buildStyleStrategy() {
        // 表头样式
        WriteCellStyle headerStyle = new WriteCellStyle();
        if (headerBackgroundColor != null) {
            headerStyle.setFillForegroundColor(parseColor(headerBackgroundColor));
            headerStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        }
        WriteFont headerFont = new WriteFont();
        if (headerFontColor != null) {
            headerFont.setColor(parseColor(headerFontColor));
        }
        if (headerFontBold != null) {
            headerFont.setBold(headerFontBold);
        }
        if (headerFontSize != null) {
            headerFont.setFontHeightInPoints(headerFontSize);
        }
        headerStyle.setWriteFont(headerFont);

        return new HorizontalCellStyleStrategy(headerStyle, new ArrayList<>());
    }

    /**
     * 解析十六进制颜色为 POI 颜色索引值。
     * 默认返回白色作为兜底。
     *
     * @param hex 十六进制颜色
     * @return 颜色索引值
     */
    private short parseColor(String hex) {
        // 简单映射常见颜色
        return switch (hex.toUpperCase()) {
            case "#F2F2F2", "#C0C0C0" -> IndexedColors.GREY_25_PERCENT.getIndex();
            case "#FFFFFF" -> IndexedColors.WHITE.getIndex();
            case "#000000" -> IndexedColors.BLACK.getIndex();
            case "#FF0000" -> IndexedColors.RED.getIndex();
            case "#0000FF" -> IndexedColors.BLUE.getIndex();
            case "#008000" -> IndexedColors.GREEN.getIndex();
            case "#FFFF00" -> IndexedColors.YELLOW.getIndex();
            case "#FFA500" -> IndexedColors.ORANGE.getIndex();
            case "#800080" -> IndexedColors.VIOLET.getIndex();
            case "#808080" -> IndexedColors.GREY_50_PERCENT.getIndex();
            default -> IndexedColors.WHITE.getIndex();
        };
    }
}
