package com.sloth.boot.starter.es.core;

import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.starter.es.document.EsUpdateDocument;
import com.sloth.boot.starter.es.query.EsPageQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 操作接口。
 * <p>
 * 定义索引管理、文档 CRUD、搜索、聚合、滚动查询等完整操作契约，
 * 由 {@link EsTemplate} 门面类实现并委托给各子模板。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface EsOperations {

    // ==================== 索引管理 ====================

    /**
     * 创建索引。
     *
     * @param index 索引名
     * @return 是否创建成功
     */
    boolean createIndex(String index);

    /**
     * 创建索引（带 Mapping 和 Settings）。
     *
     * @param index        索引名
     * @param mappingJson  Mapping JSON，为空则跳过
     * @param settingsJson Settings JSON，为空则跳过
     * @return 是否创建成功
     */
    boolean createIndex(String index, String mappingJson, String settingsJson);

    /**
     * 创建索引（基于 @Document 注解实体类）。
     *
     * @param entityClass 实体类
     * @return 是否创建成功
     */
    boolean createIndex(Class<?> entityClass);

    /**
     * 删除索引。
     *
     * @param index 索引名
     * @return 是否删除成功
     */
    boolean deleteIndex(String index);

    /**
     * 检查索引是否存在。
     *
     * @param index 索引名
     * @return 是否存在
     */
    boolean existsIndex(String index);

    /**
     * 刷新索引。
     *
     * @param index 索引名
     */
    void refreshIndex(String index);

    /**
     * 获取索引 Mapping。
     *
     * @param index 索引名
     * @return Mapping JSON 字符串
     */
    String getMapping(String index);

    /**
     * 检查别名是否存在。
     *
     * @param alias 别名
     * @return 是否存在
     */
    boolean existsAlias(String alias);

    // ==================== 文档 CRUD ====================

    /**
     * 保存文档（新增或全量替换）。
     *
     * @param entity 文档实体
     * @param <T>    文档类型
     * @return 保存后的实体
     */
    <T> T save(T entity);

    /**
     * 保存文档到指定索引。
     *
     * @param entity 文档实体
     * @param index  索引名
     * @param <T>    文档类型
     * @return 保存后的实体
     */
    <T> T save(T entity, String index);

    /**
     * 批量保存。
     *
     * @param entities 文档实体列表
     * @param <T>      文档类型
     * @return 保存后的实体列表
     */
    <T> List<T> batchSave(List<T> entities);

    /**
     * 批量保存到指定索引。
     *
     * @param entities 文档实体列表
     * @param index    索引名
     * @param <T>      文档类型
     * @return 保存后的实体列表
     */
    <T> List<T> batchSave(List<T> entities, String index);

    /**
     * 更新文档（全量覆盖）。
     *
     * @param entity 文档实体
     * @param <T>    文档类型
     * @return 更新后的实体
     */
    <T> T update(T entity);

    /**
     * 局部更新。
     *
     * @param id     文档 ID
     * @param fields 待更新的字段
     * @param clazz  文档类型
     * @param <T>    文档类型
     */
    <T> void updatePartial(String id, Map<String, Object> fields, Class<T> clazz);

    /**
     * 局部更新（基于 {@link EsUpdateDocument}）。
     * <p>支持字段更新、脚本更新、upsert 和自定义重试次数。
     *
     * @param id     文档 ID
     * @param update 更新请求体
     * @param clazz  文档类型
     * @param <T>    文档类型
     */
    <T> void updatePartial(String id, EsUpdateDocument update, Class<T> clazz);

    /**
     * 根据 ID 获取文档。
     *
     * @param id    文档 ID
     * @param clazz 文档类型
     * @param <T>   文档类型
     * @return 文档实体，不存在时返回 {@code null}
     */
    <T> T get(String id, Class<T> clazz);

    /**
     * 根据 ID 获取文档，从指定索引。
     *
     * @param id    文档 ID
     * @param clazz 文档类型
     * @param index 索引名
     * @param <T>   文档类型
     * @return 文档实体，不存在时返回 {@code null}
     */
    <T> T get(String id, Class<T> clazz, String index);

    /**
     * 删除文档。
     *
     * @param entity 文档实体
     * @param <T>    文档类型
     * @return 删除结果 ID
     */
    <T> String delete(T entity);

    /**
     * 根据 ID 删除文档。
     *
     * @param id    文档 ID
     * @param clazz 文档类型
     * @param <T>   文档类型
     * @return 删除结果 ID
     */
    <T> String deleteById(String id, Class<T> clazz);

    /**
     * 检查文档是否存在。
     *
     * @param id    文档 ID
     * @param clazz 文档类型
     * @param <T>   文档类型
     * @return 是否存在
     */
    <T> boolean exists(String id, Class<T> clazz);

    /**
     * 批量删除。
     *
     * @param ids   文档 ID 列表
     * @param clazz 文档类型
     * @param <T>   文档类型
     * @return 批量操作结果
     */
    <T> BulkResult batchDelete(List<String> ids, Class<T> clazz);

    // ==================== 搜索查询 ====================

    /**
     * 原生 Query 搜索。
     *
     * @param query 查询条件
     * @param clazz 文档类型
     * @param <T>   文档类型
     * @return 搜索命中结果
     */
    <T> SearchHits<T> search(Query query, Class<T> clazz);

    /**
     * EsPageQuery 搜索（完整结果）。
     *
     * @param pageQuery 分页查询条件
     * @param clazz     文档类型
     * @param <T>       文档类型
     * @return 搜索结果
     */
    <T> SearchResult<T> search(EsPageQuery<T> pageQuery, Class<T> clazz);

    /**
     * EsPageQuery 搜索（指定索引）。
     *
     * @param pageQuery 分页查询条件
     * @param clazz     文档类型
     * @param index     索引名
     * @param <T>       文档类型
     * @return 搜索结果
     */
    <T> SearchResult<T> search(EsPageQuery<T> pageQuery, Class<T> clazz, String index);

    /**
     * EsPageQuery 分页搜索（返回统一 PageResult）。
     *
     * @param pageQuery 分页查询条件
     * @param clazz     文档类型
     * @param <T>       文档类型
     * @return 统一分页结果
     */
    <T> PageResult<T> pageQuery(EsPageQuery<T> pageQuery, Class<T> clazz);

    /**
     * EsPageQuery 分页搜索（指定索引）。
     *
     * @param pageQuery 分页查询条件
     * @param clazz     文档类型
     * @param index     索引名
     * @param <T>       文档类型
     * @return 统一分页结果
     */
    <T> PageResult<T> pageQuery(EsPageQuery<T> pageQuery, Class<T> clazz, String index);

    /**
     * 初始化滚动查询。
     *
     * @param pageQuery 查询条件
     * @param clazz     文档类型
     * @param <T>       泛型
     * @return 搜索结果（含 scrollId）
     */
    <T> SearchResult<T> scroll(EsPageQuery<T> pageQuery, Class<T> clazz);

    /**
     * 初始化滚动查询（指定索引）。
     *
     * @param pageQuery 查询条件
     * @param clazz     文档类型
     * @param index     索引名
     * @param <T>       文档类型
     * @return 搜索结果（含 scrollId）
     */
    <T> SearchResult<T> scroll(EsPageQuery<T> pageQuery, Class<T> clazz, String index);

    /**
     * Search After 方式深分页（推荐）。
     *
     * @param pageQuery    分页查询条件
     * @param clazz        文档类型
     * @param searchAfter  上一页最后一条记录的排序值
     * @param index        索引名
     * @param <T>          文档类型
     * @return 搜索结果
     */
    <T> SearchResult<T> searchAfter(EsPageQuery<T> pageQuery, Class<T> clazz,
                                    Object[] searchAfter, String index);
}
