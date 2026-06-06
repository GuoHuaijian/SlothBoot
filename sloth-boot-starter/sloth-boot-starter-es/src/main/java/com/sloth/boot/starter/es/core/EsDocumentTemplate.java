package com.sloth.boot.starter.es.core;

import com.sloth.boot.starter.es.config.EsProperties;
import com.sloth.boot.starter.es.document.EsUpdateDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 文档 CRUD 模板。
 * <p>
 * 提供文档保存、批量保存、更新、局部更新、查询、删除、存在性检查等操作。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class EsDocumentTemplate {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EsProperties esProperties;


    /**
     * 保存文档（新增或全量替换）。
     */
    public <T> T save(T entity) {
        return elasticsearchTemplate.save(entity);
    }

    /**
     * 保存文档到指定索引。
     */
    public <T> T save(T entity, String index) {
        return elasticsearchTemplate.save(entity, IndexCoordinates.of(index));
    }

    /**
     * 批量保存。
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> batchSave(List<T> entities) {
        if (entities.isEmpty()) return entities;
        return (List<T>) elasticsearchTemplate.save(entities);
    }

    /**
     * 批量保存到指定索引。
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> batchSave(List<T> entities, String index) {
        if (entities.isEmpty()) return entities;
        return (List<T>) elasticsearchTemplate.save(entities, IndexCoordinates.of(index));
    }

    /**
     * 更新文档（全量覆盖）。
     */
    public <T> T update(T entity) {
        return elasticsearchTemplate.save(entity);
    }

    /**
     * 局部更新。
     *
     * @param id     文档 ID
     * @param fields 待更新的字段
     * @param clazz  文档类型
     */
    public <T> void updatePartial(String id, Map<String, Object> fields, Class<T> clazz) {
        UpdateQuery query = UpdateQuery.builder(id)
            .withDocument(Document.from(fields))
            .withRetryOnConflict(esProperties.getRetryOnConflict())
            .build();
        elasticsearchTemplate.update(query, IndexCoordinates.of(getIndexName(clazz)));
    }

    /**
     * 局部更新（基于 {@link EsUpdateDocument}）。
     * <p>支持字段更新、脚本更新、upsert 和自定义重试次数。
     *
     * @param id     文档 ID
     * @param update 更新请求体
     * @param clazz  文档类型
     */
    public <T> void updatePartial(String id, EsUpdateDocument update, Class<T> clazz) {
        UpdateQuery.Builder queryBuilder = UpdateQuery.builder(id);

        if (update.hasScript()) {
            queryBuilder.withScript(update.getScript());
            Map<String, Object> params = update.getScriptParams();
            if (!params.isEmpty()) {
                queryBuilder.withParams(params);
            }
            queryBuilder.withLang("painless");
            queryBuilder.withScriptedUpsert(update.isUpsert());
        } else if (update.hasFields()) {
            queryBuilder.withDocument(Document.from(update.getFields()));
            queryBuilder.withDocAsUpsert(update.isUpsert());
        }

        if (update.getRetryOnConflict() != null) {
            queryBuilder.withRetryOnConflict(update.getRetryOnConflict());
        } else {
            queryBuilder.withRetryOnConflict(esProperties.getRetryOnConflict());
        }
        elasticsearchTemplate.update(queryBuilder.build(), IndexCoordinates.of(getIndexName(clazz)));
    }

    /**
     * 根据 ID 获取文档。
     */
    public <T> T get(String id, Class<T> clazz) {
        return elasticsearchTemplate.get(id, clazz);
    }

    /**
     * 根据 ID 获取文档，从指定索引。
     */
    public <T> T get(String id, Class<T> clazz, String index) {
        return elasticsearchTemplate.get(id, clazz, IndexCoordinates.of(index));
    }

    /**
     * 删除文档。
     *
     * @return 删除结果 ID
     */
    public <T> String delete(T entity) {
        return elasticsearchTemplate.delete(entity);
    }

    /**
     * 根据 ID 删除文档。
     */
    public <T> String deleteById(String id, Class<T> clazz) {
        T entity = get(id, clazz);
        if (entity != null) {
            return elasticsearchTemplate.delete(entity);
        }
        return null;
    }

    /**
     * 检查文档是否存在。
     */
    public <T> boolean exists(String id, Class<T> clazz) {
        return elasticsearchTemplate.exists(id, clazz);
    }

    /**
     * 批量删除。
     *
     * @return 批量操作结果
     */
    public <T> BulkResult batchDelete(List<String> ids, Class<T> clazz) {
        BulkResult result = new BulkResult();
        for (String id : ids) {
            try {
                deleteById(id, clazz);
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                result.addFailItem(id, e.getMessage());
            }
        }
        result.setSuccess(result.getFailCount() == 0);
        return result;
    }


    private <T> String getIndexName(Class<T> clazz) {
        return EsTemplateUtils.getIndexName(clazz, esProperties);
    }
}
