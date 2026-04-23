package com.sloth.boot.starter.excel.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sloth.boot.starter.excel.listener.ExcelReadListener;
import com.sloth.boot.starter.excel.model.SheetData;
import com.sloth.boot.starter.excel.wrapper.ExcelResponseWrapper;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 工具类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ExcelUtil {

    private ExcelUtil() {
    }

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
    public static <T> void export(HttpServletResponse response, String fileName, Class<T> head, List<T> data) throws IOException {
        ExcelResponseWrapper.wrap(response, fileName);
        EasyExcel.write(response.getOutputStream(), head)
                .sheet("Sheet1")
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
    public static void export(HttpServletResponse response, String fileName, List<SheetData> sheets) throws IOException {
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
     * 导入 Excel。
     *
     * @param file     文件
     * @param clazz    类型
     * @param listener 监听器
     * @param <T>      泛型
     * @return 数据列表
     * @throws IOException IO 异常
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz, ExcelReadListener<T> listener) throws IOException {
        EasyExcel.read(file.getInputStream(), clazz, listener).sheet().doRead();
        return new ArrayList<>(listener.getCachedData());
    }

    /**
     * 下载模板。
     *
     * @param response 响应对象
     * @param fileName 文件名
     * @param head     表头
     * @param <T>      泛型
     * @throws IOException IO 异常
     */
    public static <T> void downloadTemplate(HttpServletResponse response, String fileName, Class<T> head) throws IOException {
        export(response, fileName, head, List.of());
    }
}
