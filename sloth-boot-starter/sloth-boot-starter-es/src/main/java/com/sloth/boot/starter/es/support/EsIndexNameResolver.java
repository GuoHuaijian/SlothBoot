package com.sloth.boot.starter.es.support;

import com.sloth.boot.starter.es.config.EsProperties;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 索引名解析器。
 * <p>
 * 支持索引前缀和按日期后缀的索引名解析，适用于日志类时间序列索引场景。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class EsIndexNameResolver {

    private final EsProperties esProperties;

    /**
     * 解析索引名，添加前缀（如有）。
     *
     * @param rawIndex 原始索引名
     * @return 完整索引名
     */
    public String resolve(String rawIndex) {
        String prefix = esProperties.getIndexPrefix();
        return prefix != null && !prefix.isEmpty() ? prefix + rawIndex : rawIndex;
    }

    /**
     * 解析带日期后缀的索引名。
     *
     * @param rawIndex 原始索引名
     * @param date     日期
     * @return 完整索引名
     */
    public String resolveWithDate(String rawIndex, LocalDate date) {
        String index = resolve(rawIndex);
        String suffix = date.format(DateTimeFormatter.ofPattern(esProperties.getIndexDateFormat()));
        return index + "_" + suffix;
    }

    /**
     * 解析今日索引名。
     *
     * @param rawIndex 原始索引名
     * @return 完整索引名
     */
    public String resolveToday(String rawIndex) {
        return resolveWithDate(rawIndex, LocalDate.now());
    }
}
