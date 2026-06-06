package com.sloth.boot.starter.es.core;

import com.sloth.boot.starter.es.config.EsProperties;
import org.springframework.util.StringUtils;

/**
 * ES 模板共用工具方法。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
final class EsTemplateUtils {

    private EsTemplateUtils() {
    }

    /**
     * 解析实体类上的 {@code @Document} 注解获取索引名。
     * 若未注解或索引名为空，回退到 {@link EsProperties#getDefaultIndex()}，
     * 再回退到类名小写。
     */
    static <T> String getIndexName(Class<T> clazz, EsProperties esProperties) {
        org.springframework.data.elasticsearch.annotations.Document document =
            clazz.getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class);
        if (document != null && StringUtils.hasText(document.indexName())) {
            return document.indexName();
        }
        String index = esProperties.getDefaultIndex();
        if (index != null) return index;
        return clazz.getSimpleName().toLowerCase();
    }
}
