package com.sloth.boot.starter.excel.config;

import com.sloth.boot.starter.excel.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Excel 操作模板。
 * <p>
 * 封装 {@link ExcelUtil} 静态方法为实例方法，支持注入和自定义。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class ExcelTemplate {

    /**
     * 导出 Excel 到输出流。
     *
     * @param data       数据列表
     * @param clazz      数据类（带 EasyExcel 注解）
     * @param outputStream 输出流
     * @param <T>        数据类型
     */
    public <T> void export(List<T> data, Class<T> clazz, OutputStream outputStream) {
        log.debug("[Excel] export {} rows, class={}", data.size(), clazz.getSimpleName());
        ExcelUtil.export(data, clazz, outputStream);
    }

    /**
     * 从输入流读取 Excel。
     *
     * @param inputStream 输入流
     * @param clazz       数据类（带 EasyExcel 注解）
     * @param <T>         数据类型
     * @return 数据列表
     */
    public <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) {
        log.debug("[Excel] import class={}", clazz.getSimpleName());
        return ExcelUtil.importExcel(inputStream, clazz);
    }
}
