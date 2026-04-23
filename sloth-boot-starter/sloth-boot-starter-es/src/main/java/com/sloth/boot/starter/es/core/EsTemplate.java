package com.sloth.boot.starter.es.core;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Elasticsearch 操作模板。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class EsTemplate {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 创建索引。
     *
     * @param index 索引名
     * @return 是否成功
     */
    public boolean createIndex(String index) {
        return elasticsearchOperations.indexOps(IndexCoordinates.of(index)).create();
    }

    /**
     * 删除索引。
     *
     * @param index 索引名
     * @return 是否成功
     */
    public boolean deleteIndex(String index) {
        return elasticsearchOperations.indexOps(IndexCoordinates.of(index)).delete();
    }

    /**
     * 检查索引是否存在。
     *
     * @param index 索引名
     * @return 是否存在
     */
    public boolean existIndex(String index) {
        return elasticsearchOperations.indexOps(IndexCoordinates.of(index)).exists();
    }

    /**
     * 保存文档。
     *
     * @param entity 文档
     * @param <T>    文档类型
     * @return 保存后的文档
     */
    public <T> T save(T entity) {
        return elasticsearchOperations.save(entity);
    }

    /**
     * 批量保存文档。
     *
     * @param entities 文档集合
     * @param <T>      文档类型
     * @return 保存后的文档集合
     */
    public <T> Iterable<T> batchSave(List<T> entities) {
        return elasticsearchOperations.save(entities);
    }

    /**
     * 更新文档。
     *
     * @param entity 文档
     * @param <T>    文档类型
     * @return 更新后的文档
     */
    public <T> T update(T entity) {
        return elasticsearchOperations.save(entity);
    }

    /**
     * 删除文档。
     *
     * @param entity 文档
     * @param <T>    文档类型
     * @return 删除结果
     */
    public <T> String delete(T entity) {
        return elasticsearchOperations.delete(entity);
    }

    /**
     * 按查询条件搜索。
     *
     * @param query 查询
     * @param clazz 目标类型
     * @param <T>   目标类型
     * @return 搜索结果
     */
    public <T> SearchHits<T> search(org.springframework.data.elasticsearch.core.query.Query query, Class<T> clazz) {
        return elasticsearchOperations.search(query, clazz);
    }

    /**
     * 分页搜索。
     *
     * @param query    ES 查询
     * @param pageable 分页参数
     * @param clazz    目标类型
     * @param <T>      目标类型
     * @return 分页结果
     */
    public <T> Page<T> page(Query query, Pageable pageable, Class<T> clazz) {
        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(query)
                .withPageable(pageable)
                .build();
        SearchHits<T> searchHits = elasticsearchOperations.search(nativeQuery, clazz);
        List<T> content = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }

    /**
     * 高亮搜索。
     *
     * @param query 查询
     * @param clazz 目标类型
     * @param <T>   目标类型
     * @return 高亮结果
     */
    public <T> List<Map<String, Object>> highlight(Query query, Class<T> clazz) {
        NativeQuery nativeQuery = new NativeQueryBuilder().withQuery(query).build();
        SearchHits<T> searchHits = elasticsearchOperations.search(nativeQuery, clazz);
        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    Map<String, Object> result = new HashMap<>(4);
                    result.put("content", hit.getContent());
                    result.put("highlightFields", hit.getHighlightFields());
                    result.put("score", hit.getScore());
                    return result;
                })
                .collect(Collectors.toList());
    }
}
