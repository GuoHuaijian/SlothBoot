package com.sloth.boot.starter.es.core;

import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.starter.es.document.EsUpdateDocument;
import com.sloth.boot.starter.es.query.EsPageQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 操作模板（门面）。
 * <p>
 * 委托 {@link EsIndexTemplate}、{@link EsDocumentTemplate}、{@link EsSearchTemplate}
 * 提供索引管理、文档 CRUD、搜索、聚合、滚动查询等完整操作。
 * <p>
 * 保持与原 EsTemplate 完全一致的方法签名，确保向后兼容。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class EsTemplate implements EsOperations {

    private final EsIndexTemplate indexTemplate;
    private final EsDocumentTemplate documentTemplate;
    private final EsSearchTemplate searchTemplate;


    // ==================== 索引管理（委托 EsIndexTemplate）====================

    /**
     * 创建索引。
     */
    public boolean createIndex(String index) {
        return indexTemplate.createIndex(index);
    }

    /**
     * 创建索引（带 Mapping 和 Settings）。
     *
     * @param index        索引名
     * @param mappingJson  Mapping JSON，为空则跳过
     * @param settingsJson Settings JSON，为空则跳过
     */
    public boolean createIndex(String index, String mappingJson, String settingsJson) {
        return indexTemplate.createIndex(index, mappingJson, settingsJson);
    }

    /**
     * 创建索引（基于 @Document 注解实体类）。
     */
    public boolean createIndex(Class<?> entityClass) {
        return indexTemplate.createIndex(entityClass);
    }

    /**
     * 删除索引。
     */
    public boolean deleteIndex(String index) {
        return indexTemplate.deleteIndex(index);
    }

    /**
     * 检查索引是否存在。
     */
    public boolean existsIndex(String index) {
        return indexTemplate.existsIndex(index);
    }

    /**
     * 刷新索引。
     */
    public void refreshIndex(String index) {
        indexTemplate.refreshIndex(index);
    }

    /**
     * 获取索引 Mapping。
     */
    public String getMapping(String index) {
        return indexTemplate.getMapping(index);
    }

    /**
     * 检查别名是否存在。
     */
    public boolean existsAlias(String alias) {
        return indexTemplate.existsAlias(alias);
    }


    // ==================== 文档 CRUD（委托 EsDocumentTemplate）====================

    /**
     * 保存文档（新增或全量替换）。
     */
    public <T> T save(T entity) {
        return documentTemplate.save(entity);
    }

    /**
     * 保存文档到指定索引。
     */
    public <T> T save(T entity, String index) {
        return documentTemplate.save(entity, index);
    }

    /**
     * 批量保存。
     */
    public <T> List<T> batchSave(List<T> entities) {
        return documentTemplate.batchSave(entities);
    }

    /**
     * 批量保存到指定索引。
     */
    public <T> List<T> batchSave(List<T> entities, String index) {
        return documentTemplate.batchSave(entities, index);
    }

    /**
     * 更新文档（全量覆盖）。
     */
    public <T> T update(T entity) {
        return documentTemplate.update(entity);
    }

    /**
     * 局部更新。
     *
     * @param id     文档 ID
     * @param fields 待更新的字段
     * @param clazz  文档类型
     */
    public <T> void updatePartial(String id, Map<String, Object> fields, Class<T> clazz) {
        documentTemplate.updatePartial(id, fields, clazz);
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
        documentTemplate.updatePartial(id, update, clazz);
    }

    /**
     * 根据 ID 获取文档。
     */
    public <T> T get(String id, Class<T> clazz) {
        return documentTemplate.get(id, clazz);
    }

    /**
     * 根据 ID 获取文档，从指定索引。
     */
    public <T> T get(String id, Class<T> clazz, String index) {
        return documentTemplate.get(id, clazz, index);
    }

    /**
     * 删除文档。
     *
     * @return 删除结果 ID
     */
    public <T> String delete(T entity) {
        return documentTemplate.delete(entity);
    }

    /**
     * 根据 ID 删除文档。
     */
    public <T> String deleteById(String id, Class<T> clazz) {
        return documentTemplate.deleteById(id, clazz);
    }

    /**
     * 检查文档是否存在。
     */
    public <T> boolean exists(String id, Class<T> clazz) {
        return documentTemplate.exists(id, clazz);
    }

    /**
     * 批量删除。
     *
     * @return 批量操作结果
     */
    public <T> BulkResult batchDelete(List<String> ids, Class<T> clazz) {
        return documentTemplate.batchDelete(ids, clazz);
    }


    // ==================== 搜索查询（委托 EsSearchTemplate）====================

    /**
     * 原生 Query 搜索。
     */
    public <T> SearchHits<T> search(Query query, Class<T> clazz) {
        return searchTemplate.search(query, clazz);
    }

    /**
     * EsPageQuery 搜索（完整结果）。
     */
    public <T> SearchResult<T> search(EsPageQuery<T> pageQuery, Class<T> clazz) {
        return searchTemplate.search(pageQuery, clazz);
    }

    /**
     * EsPageQuery 搜索（指定索引）。
     */
    public <T> SearchResult<T> search(EsPageQuery<T> pageQuery, Class<T> clazz, String index) {
        return searchTemplate.search(pageQuery, clazz, index);
    }

    /**
     * EsPageQuery 分页搜索（返回统一 PageResult）。
     */
    public <T> PageResult<T> pageQuery(EsPageQuery<T> pageQuery, Class<T> clazz) {
        return searchTemplate.pageQuery(pageQuery, clazz);
    }

    /**
     * EsPageQuery 分页搜索（指定索引）。
     */
    public <T> PageResult<T> pageQuery(EsPageQuery<T> pageQuery, Class<T> clazz, String index) {
        return searchTemplate.pageQuery(pageQuery, clazz, index);
    }

    /**
     * 初始化滚动查询。
     *
     * @param pageQuery 查询条件
     * @param clazz     文档类型
     * @param <T>       泛型
     * @return 搜索结果（含 scrollId）
     */
    public <T> SearchResult<T> scroll(EsPageQuery<T> pageQuery, Class<T> clazz) {
        return searchTemplate.scroll(pageQuery, clazz);
    }

    /**
     * 初始化滚动查询（指定索引）。
     */
    public <T> SearchResult<T> scroll(EsPageQuery<T> pageQuery, Class<T> clazz, String index) {
        return searchTemplate.scroll(pageQuery, clazz, index);
    }

    /**
     * Search After 方式深分页（推荐）。
     */
    public <T> SearchResult<T> searchAfter(EsPageQuery<T> pageQuery, Class<T> clazz,
                                            Object[] searchAfter, String index) {
        return searchTemplate.searchAfter(pageQuery, clazz, searchAfter, index);
    }
}
