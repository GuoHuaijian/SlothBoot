package com.sloth.boot.starter.excel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Excel 全局配置属性。
 * <p>
 * 统一以 {@code sloth.excel} 为前缀，控制导入导出、模板、样式等行为。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.excel")
public class ExcelProperties {

    /**
     * 是否启用 Excel Starter。
     */
    private boolean enabled = true;

    /**
     * 导入配置。
     */
    private Import importConfig = new Import();

    /**
     * 导出配置。
     */
    private Export exportConfig = new Export();

    /**
     * 导入配置项。
     */
    @Data
    public static class Import {

        /**
         * 每批处理的行数。
         */
        private int batchSize = 1000;

        /**
         * 读取时是否忽略解析异常继续处理。
         */
        private boolean ignoreErrors = false;

        /**
         * 是否启用数据校验（集成 Bean Validation）。
         */
        private boolean validate = false;

        /**
         * 表头行号（从 1 开始），用于跳过非数据行。
         */
        private int headRowNumber = 1;
    }

    /**
     * 导出配置项。
     */
    @Data
    public static class Export {

        /**
         * 默认 Sheet 名称。
         */
        private String defaultSheetName = "Sheet1";

        /**
         * 是否自动调整列宽。
         */
        private boolean autoSizeColumn = true;

        /**
         * 样式配置。
         */
        private Style style = new Style();
    }

    /**
     * 导出样式配置项。
     */
    @Data
    public static class Style {

        /**
         * 表头背景色（十六进制 RGB）。
         */
        private String headerBackgroundColor = "#F2F2F2";

        /**
         * 表头字体颜色（十六进制 RGB）。
         */
        private String headerFontColor = "#000000";

        /**
         * 表头字体是否加粗。
         */
        private boolean headerFontBold = true;

        /**
         * 表头字体大小。
         */
        private short headerFontSize = 11;
    }
}
