package com.sloth.boot.starter.excel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sloth.boot.starter.excel.listener.ExcelReadListener;
import com.sloth.boot.starter.excel.listener.StreamingExcelReadListener;
import com.sloth.boot.starter.excel.model.ExcelExportBuilder;
import com.sloth.boot.starter.excel.model.ExcelImportResult;
import com.sloth.boot.starter.excel.model.SheetData;
import com.sloth.boot.starter.excel.wrapper.ExcelResponseWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Excel 工具类。
 * <p>
 * 提供单/多 Sheet 导出、导入（含流式）、模板下载及自定义样式等能力。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ExcelUtil {

    private ExcelUtil() {
    }

    // ==================== 导出 ====================

    /**
     * 单 Sheet 导出。
     *
     * @param response 响应对象
     * @param fileName 文件名
     * @param head     表头类型
     * @param data     数据
     * @param <T>      泛型
     * @throws IOException IO 异常
     */
    public static <T> void export(HttpServletResponse response, String fileName, Class<T> head, List<T> data)
            throws IOException {
        ExcelResponseWrapper.wrap(response, fileName);
        EasyExcel.write(response.getOutputStream(), head)
            .sheet("Sheet1")
            .doWrite(data);
    }

    /**
     * 单 Sheet 导出（指定 Sheet 名称）。
     *
     * @param response  响应对象
     * @param fileName  文件名
     * @param sheetName Sheet 名称
     * @param head      表头类型
     * @param data      数据
     * @param <T>       泛型
     * @throws IOException IO 异常
     */
    public static <T> void export(HttpServletResponse response, String fileName, String sheetName,
                                  Class<T> head, List<T> data) throws IOException {
        ExcelResponseWrapper.wrap(response, fileName);
        EasyExcel.write(response.getOutputStream(), head)
            .sheet(sheetName)
            .doWrite(data);
    }

    /**
     * 多 Sheet 导出。
     *
     * @param response 响应对象
     * @param fileName 文件名
     * @param sheets   Sheet 数据
     * @throws IOException IO 异常
     */
    public static void export(HttpServletResponse response, String fileName, List<SheetData> sheets)
            throws IOException {
        ExcelResponseWrapper.wrap(response, fileName);
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build()) {
            int index = 0;
            for (SheetData sheetData : sheets) {
                WriteSheet writeSheet = EasyExcel.writerSheet(index++, sheetData.getSheetName())
                    .head(sheetData.getHead())
                    .build();
                excelWriter.write(sheetData.getData(), writeSheet);
            }
        }
    }

    /**
     * 使用构建器执行自定义导出。
     *
     * @param response 响应对象
     * @param builder  导出构建器
     * @param <T>      泛型
     * @throws IOException IO 异常
     */
    public static <T> void export(HttpServletResponse response, ExcelExportBuilder<T> builder) throws IOException {
        ExcelResponseWrapper.wrap(response, builder.getFileName());
        builder.write(response.getOutputStream());
    }

    // ==================== 导入 ====================

    /**
     * 导入 Excel（全量缓存）。
     *
     * @param file     文件
     * @param clazz    类型
     * @param listener 监听器
     * @param <T>      泛型
     * @return 数据列表
     * @throws IOException IO 异常
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz, ExcelReadListener<T> listener)
            throws IOException {
        try (InputStream in = file.getInputStream()) {
            EasyExcel.read(in, clazz, listener).sheet().doRead();
        }
        return new ArrayList<>(listener.getCachedData());
    }

    /**
     * 导入 Excel（全量缓存，使用默认监听器）。
     *
     * @param file  文件
     * @param clazz 类型
     * @param <T>   泛型
     * @return 导入结果
     * @throws IOException IO 异常
     */
    public static <T> ExcelImportResult<T> importExcel(MultipartFile file, Class<T> clazz) throws IOException {
        ExcelReadListener<T> listener = new ExcelReadListener<>();
        try (InputStream in = file.getInputStream()) {
            EasyExcel.read(in, clazz, listener).sheet().doRead();
        }
        ExcelImportResult<T> result = new ExcelImportResult<>();
        result.setData(new ArrayList<>(listener.getCachedData()));
        result.setErrors(listener.getErrorDetails());
        result.setSuccessRows(listener.getCachedData().size());
        result.setFailedRows(listener.getErrorDetails().size());
        result.setAllSuccess(listener.getErrorDetails().isEmpty());
        return result;
    }

    /**
     * 导入 Excel（全量缓存，使用默认监听器，指定批次大小）。
     *
     * @param file      文件
     * @param clazz     类型
     * @param batchSize 每批处理的行数
     * @param consumer  分批处理器
     * @param <T>       泛型
     * @return 数据列表
     * @throws IOException IO 异常
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz, int batchSize,
                                          Consumer<List<T>> consumer) throws IOException {
        ExcelReadListener<T> listener = new ExcelReadListener<>(consumer, batchSize, true);
        try (InputStream in = file.getInputStream()) {
            EasyExcel.read(in, clazz, listener).sheet().doRead();
        }
        return new ArrayList<>(listener.getCachedData());
    }

    /**
     * 导入 Excel（全量缓存，使用默认监听器，指定批次大小、跳过行数）。
     *
     * @param file          文件
     * @param clazz         类型
     * @param batchSize     每批处理的行数
     * @param headRowNumber 表头行号（从 1 开始），用于跳过非数据行
     * @param consumer      分批处理器
     * @param <T>           泛型
     * @return 数据列表
     * @throws IOException IO 异常
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz, int batchSize, int headRowNumber,
                                          Consumer<List<T>> consumer) throws IOException {
        ExcelReadListener<T> listener = new ExcelReadListener<>(consumer, batchSize, true);
        try (InputStream in = file.getInputStream()) {
            EasyExcel.read(in, clazz, listener)
                .sheet()
                .headRowNumber(headRowNumber)
                .doRead();
        }
        return new ArrayList<>(listener.getCachedData());
    }

    /**
     * 流式导入 Excel（不缓存所有数据，适用于大文件）。
     *
     * @param file          文件
     * @param clazz         类型
     * @param batchSize     每批处理的行数
     * @param headRowNumber 表头行号（从 1 开始）
     * @param consumer      分批处理器
     * @param <T>           泛型
     * @return 流式读取监听器（含错误信息）
     * @throws IOException IO 异常
     */
    public static <T> StreamingExcelReadListener<T> importExcelStreaming(
            MultipartFile file, Class<T> clazz, int batchSize, int headRowNumber,
            Consumer<List<T>> consumer) throws IOException {
        StreamingExcelReadListener<T> listener = new StreamingExcelReadListener<>(consumer, batchSize);
        try (InputStream in = file.getInputStream()) {
            EasyExcel.read(in, clazz, listener)
                .sheet()
                .headRowNumber(headRowNumber)
                .doRead();
        }
        return listener;
    }

    /**
     * 流式导入 Excel（默认批次大小 1000，表头行号 1）。
     *
     * @param file     文件
     * @param clazz    类型
     * @param consumer 分批处理器
     * @param <T>      泛型
     * @return 流式读取监听器（含错误信息）
     * @throws IOException IO 异常
     */
    public static <T> StreamingExcelReadListener<T> importExcelStreaming(
            MultipartFile file, Class<T> clazz, Consumer<List<T>> consumer) throws IOException {
        return importExcelStreaming(file, clazz, 1000, 1, consumer);
    }

    // ==================== 模板 ====================

    /**
     * 下载模板（仅生成空白标题行）。
     *
     * @param response 响应对象
     * @param fileName 文件名
     * @param head     表头
     * @param <T>      泛型
     * @throws IOException IO 异常
     */
    public static <T> void downloadTemplate(HttpServletResponse response, String fileName, Class<T> head)
            throws IOException {
        export(response, fileName, head, List.of());
    }

    /**
     * 下载模板（带示例数据）。
     *
     * @param response   响应对象
     * @param fileName   文件名
     * @param head       表头
     * @param sampleData 示例数据
     * @param <T>        泛型
     * @throws IOException IO 异常
     */
    public static <T> void downloadTemplate(HttpServletResponse response, String fileName, Class<T> head,
                                            List<T> sampleData) throws IOException {
        ExcelResponseWrapper.wrap(response, fileName);
        EasyExcel.write(response.getOutputStream(), head)
            .sheet("模板")
            .doWrite(sampleData);
    }

    // ==================== 构建器工厂 ====================

    /**
     * 创建导出构建器。
     *
     * @param fileName 文件名
     * @param head     表头类型
     * @param data     数据
     * @param <T>      泛型
     * @return 导出构建器
     */
    public static <T> ExcelExportBuilder<T> exportBuilder(String fileName, Class<T> head, List<T> data) {
        return new ExcelExportBuilder<T>(fileName, head, data)
            .sheetName("Sheet1");
    }
}
