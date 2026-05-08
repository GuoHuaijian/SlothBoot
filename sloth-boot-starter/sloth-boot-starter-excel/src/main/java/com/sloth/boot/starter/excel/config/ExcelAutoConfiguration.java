package com.sloth.boot.starter.excel.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Excel 自动配置。
 * <p>
 * 当 EasyExcel 在 classpath 上且未禁用时激活。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(name = "com.alibaba.excel.EasyExcel")
@ConditionalOnProperty(prefix = "sloth.excel", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExcelAutoConfiguration {
}
