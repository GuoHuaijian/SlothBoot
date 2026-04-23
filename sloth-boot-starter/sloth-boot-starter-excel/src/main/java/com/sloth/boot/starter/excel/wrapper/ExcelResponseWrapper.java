package com.sloth.boot.starter.excel.wrapper;

import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Excel 响应包装器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ExcelResponseWrapper {

    private ExcelResponseWrapper() {
    }

    /**
     * 包装 Excel 下载响应头。
     *
     * @param response 响应对象
     * @param fileName 文件名
     */
    public static void wrap(HttpServletResponse response, String fileName) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''"
                + URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8));
    }
}
