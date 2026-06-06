package com.sloth.boot.starter.es.core;

import com.sloth.boot.starter.es.config.EsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.util.Map;

/**
 * Elasticsearch 索引管理模板。
 * <p>
 * 提供索引创建、删除、存在性检查、刷新、Mapping 查询、别名检查等操作。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class EsIndexTemplate {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EsProperties esProperties;


    /**
     * 创建索引。
     */
    public boolean createIndex(String index) {
        return elasticsearchTemplate.indexOps(IndexCoordinates.of(index)).create();
    }

    /**
     * 创建索引（带 Mapping 和 Settings）。
     *
     * @param index        索引名
     * @param mappingJson  Mapping JSON，为空则跳过
     * @param settingsJson Settings JSON，为空则跳过
     */
    public boolean createIndex(String index, String mappingJson, String settingsJson) {
        IndexOperations ops = elasticsearchTemplate.indexOps(IndexCoordinates.of(index));
        if (settingsJson != null && mappingJson != null) {
            Map<String, Object> settings = Document.parse(settingsJson);
            Document mapping = Document.parse(mappingJson);
            return ops.create(settings, mapping);
        } else if (settingsJson != null) {
            Map<String, Object> settings = Document.parse(settingsJson);
            return ops.create(settings);
        } else if (mappingJson != null) {
            boolean created = ops.create();
            if (created) {
                ops.putMapping(Document.parse(mappingJson));
            }
            return created;
        }
        return ops.create();
    }

    /**
     * 创建索引（基于 @Document 注解实体类）。
     */
    public boolean createIndex(Class<?> entityClass) {
        return elasticsearchTemplate.indexOps(entityClass).create();
    }

    /**
     * 删除索引。
     */
    public boolean deleteIndex(String index) {
        return elasticsearchTemplate.indexOps(IndexCoordinates.of(index)).delete();
    }

    /**
     * 检查索引是否存在。
     */
    public boolean existsIndex(String index) {
        return elasticsearchTemplate.indexOps(IndexCoordinates.of(index)).exists();
    }

    /**
     * 刷新索引。
     */
    public void refreshIndex(String index) {
        elasticsearchTemplate.indexOps(IndexCoordinates.of(index)).refresh();
    }

    /**
     * 获取索引 Mapping。
     */
    public String getMapping(String index) {
        return elasticsearchTemplate.indexOps(IndexCoordinates.of(index))
            .createMapping().toString();
    }

    /**
     * 检查别名是否存在。
     */
    public boolean existsAlias(String alias) {
        return elasticsearchTemplate.indexOps(IndexCoordinates.of(alias)).exists();
    }
}
