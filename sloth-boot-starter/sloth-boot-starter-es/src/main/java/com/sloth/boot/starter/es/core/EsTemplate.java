package com.sloth.boot.starter.es.core;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.starter.es.config.EsProperties;
import com.sloth.boot.starter.es.document.EsUpdateDocument;
import com.sloth.boot.starter.es.query.EsHighlightConfig;
import com.sloth.boot.starter.es.query.EsPageQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Elasticsearch 操作模板。
 * <p>
 * 提供索引管理、文档 CRUD、搜索、聚合、滚动查询等完整操作。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class EsTemplate {

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
        var ops = elasticsearchTemplate.indexOps(IndexCoordinates.of(index));
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


    /**
     * 原生 Query 搜索。
     */
    public <T> SearchHits<T> search(org.springframework.data.elasticsearch.core.query.Query query, Class<T> clazz) {
        return elasticsearchTemplate.search(query, clazz);
    }

    /**
     * EsPageQuery 搜索（完整结果）。
     */
    public <T> SearchResult<T> search(EsPageQuery<T> pageQuery, Class<T> clazz) {
        return doSearch(pageQuery, clazz, null);
    }

    /**
     * EsPageQuery 搜索（指定索引）。
     */
    public <T> SearchResult<T> search(EsPageQuery<T> pageQuery, Class<T> clazz, String index) {
        return doSearch(pageQuery, clazz, index);
    }

    /**
     * EsPageQuery 分页搜索（返回统一 PageResult）。
     */
    public <T> PageResult<T> pageQuery(EsPageQuery<T> pageQuery, Class<T> clazz) {
        SearchHits<T> hits = executeNativeQuery(pageQuery, clazz, null);
        List<T> list = hits.getSearchHits().stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
        return PageResult.of(list, hits.getTotalHits(), pageQuery.getPageNum(), pageQuery.getPageSize());
    }

    /**
     * EsPageQuery 分页搜索（指定索引）。
     */
    public <T> PageResult<T> pageQuery(EsPageQuery<T> pageQuery, Class<T> clazz, String index) {
        SearchHits<T> hits = executeNativeQuery(pageQuery, clazz, index);
        List<T> list = hits.getSearchHits().stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
        return PageResult.of(list, hits.getTotalHits(), pageQuery.getPageNum(), pageQuery.getPageSize());
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
        return scroll(pageQuery, clazz, null);
    }

    /**
     * 初始化滚动查询（指定索引）。
     */
    public <T> SearchResult<T> scroll(EsPageQuery<T> pageQuery, Class<T> clazz, String index) {
        Query boolQuery = buildBoolQuery(pageQuery);
        NativeQueryBuilder builder = new NativeQueryBuilder()
            .withQuery(boolQuery)
            .withPageable(PageRequest.of(0, esProperties.getScrollSize()));
        applySourceFiltering(builder, pageQuery);

        if (!pageQuery.getSorts().isEmpty()) {
            for (EsPageQuery.SortConfig sort : pageQuery.getSorts()) {
                builder.withSort(SortOptions.of(s -> s.field(f -> f.field(sort.getField()).order(sort.getOrder()))));
            }
        }

        NativeQuery nativeQuery = builder.build();
        SearchHits<T> hits;
        if (index != null) {
            hits = elasticsearchTemplate.search(nativeQuery, clazz, IndexCoordinates.of(index));
        } else {
            hits = elasticsearchTemplate.search(nativeQuery, clazz);
        }

        SearchResult<T> result = buildSearchResult(hits, pageQuery);
        return result;
    }

    /**
     * Search After 方式深分页（推荐）。
     */
    public <T> SearchResult<T> searchAfter(EsPageQuery<T> pageQuery, Class<T> clazz,
                                            Object[] searchAfter, String index) {
        Query boolQuery = buildBoolQuery(pageQuery);
        NativeQueryBuilder builder = new NativeQueryBuilder()
            .withQuery(boolQuery)
            .withPageable(PageRequest.of(0, pageQuery.getPageSize()));
        applySourceFiltering(builder, pageQuery);

        if (!pageQuery.getSorts().isEmpty()) {
            for (EsPageQuery.SortConfig sort : pageQuery.getSorts()) {
                builder.withSort(SortOptions.of(s -> s.field(f -> f.field(sort.getField()).order(sort.getOrder()))));
            }
        }

        if (searchAfter != null) {
            builder.withSearchAfter(Arrays.asList(searchAfter));
        }

        NativeQuery nativeQuery = builder.build();
        IndexCoordinates indexCoord = index != null ? IndexCoordinates.of(index) : null;
        SearchHits<T> hits = indexCoord != null
            ? elasticsearchTemplate.search(nativeQuery, clazz, indexCoord)
            : elasticsearchTemplate.search(nativeQuery, clazz);

        SearchResult<T> result = buildSearchResult(hits, pageQuery);
        if (!hits.getSearchHits().isEmpty()) {
            result.setScrollId(hits.getSearchHits().get(hits.getSearchHits().size() - 1).getSortValues().toString());
        }
        return result;
    }


    private <T> SearchResult<T> doSearch(EsPageQuery<T> pageQuery, Class<T> clazz, String index) {
        SearchHits<T> hits = executeNativeQuery(pageQuery, clazz, index);
        return buildSearchResult(hits, pageQuery);
    }

    @SuppressWarnings("unchecked")
    private <T> SearchHits<T> executeNativeQuery(EsPageQuery<T> pageQuery, Class<T> clazz, String index) {
        Query boolQuery = buildBoolQuery(pageQuery);
        NativeQueryBuilder builder = new NativeQueryBuilder()
            .withQuery(boolQuery)
            .withPageable(PageRequest.of(pageQuery.getPageNum() - 1, pageQuery.getPageSize()));
        applySourceFiltering(builder, pageQuery);

        // 排序
        if (!pageQuery.getSorts().isEmpty()) {
            for (EsPageQuery.SortConfig sort : pageQuery.getSorts()) {
                builder.withSort(SortOptions.of(s -> s.field(f -> f.field(sort.getField()).order(sort.getOrder()))));
            }
        }

        // 高亮
        if (pageQuery.getHighlight() != null) {
            applyHighlight(builder, pageQuery.getHighlight());
        }

        NativeQuery nativeQuery = builder.build();

        if (index != null) {
            return elasticsearchTemplate.search(nativeQuery, clazz, IndexCoordinates.of(index));
        }
        return elasticsearchTemplate.search(nativeQuery, clazz);
    }

    private Query buildBoolQuery(EsPageQuery<?> pageQuery) {
        if (pageQuery.getQuery() == null && pageQuery.getFilter() == null) {
            return QueryBuilders.matchAll(m -> m);
        }

        var boolBuilder = new BoolQuery.Builder();
        if (pageQuery.getQuery() != null) {
            boolBuilder.must(pageQuery.getQuery().getQuery());
        }
        if (pageQuery.getFilter() != null) {
            boolBuilder.filter(pageQuery.getFilter().getQuery());
        }
        return boolBuilder.build()._toQuery();
    }

    private void applySourceFiltering(NativeQueryBuilder builder, EsPageQuery<?> pageQuery) {
        if (pageQuery.getSourceIncludes() != null || pageQuery.getSourceExcludes() != null) {
            FetchSourceFilterBuilder fb = new FetchSourceFilterBuilder();
            if (pageQuery.getSourceIncludes() != null) {
                fb.withIncludes(pageQuery.getSourceIncludes().toArray(new String[0]));
            }
            if (pageQuery.getSourceExcludes() != null) {
                fb.withExcludes(pageQuery.getSourceExcludes().toArray(new String[0]));
            }
            builder.withSourceFilter(fb.build());
        }
    }

    private void applyHighlight(NativeQueryBuilder builder, EsHighlightConfig config) {
        List<String> fields = config.getFields();
        if (fields == null || fields.isEmpty()) return;

        HighlightFieldParameters.HighlightFieldParametersBuilder paramsBuilder =
            HighlightFieldParameters.builder()
                .withPreTags(config.getPreTag())
                .withPostTags(config.getPostTag());

        if (config.getFragmentSize() > 0) {
            paramsBuilder.withFragmentSize(config.getFragmentSize());
        }
        if (config.getNumberOfFragments() > 0) {
            paramsBuilder.withNumberOfFragments(config.getNumberOfFragments());
        }
        if (config.getType() != null) {
            paramsBuilder.withType(config.getType().name());
        }

        HighlightFieldParameters params = paramsBuilder.build();

        List<HighlightField> highlightFields = fields.stream()
            .map(field -> new HighlightField(field, params))
            .collect(Collectors.toList());

        Highlight highlight = new Highlight(highlightFields);
        HighlightQuery highlightQuery = new HighlightQuery(highlight, null);
        builder.withHighlightQuery(highlightQuery);
    }

    private <T> SearchResult<T> buildSearchResult(SearchHits<T> hits, EsPageQuery<?> pageQuery) {
        SearchResult<T> result = new SearchResult<>();
        List<T> records = hits.getSearchHits().stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
        result.setRecords(records);
        result.setTotal(hits.getTotalHits());
        result.setMaxScore(hits.getMaxScore());

        // 高亮
        if (pageQuery.getHighlight() != null) {
            Map<String, Map<String, List<String>>> highlights = new HashMap<>();
            for (SearchHit<T> hit : hits.getSearchHits()) {
                if (!hit.getHighlightFields().isEmpty()) {
                    highlights.put(hit.getId(), hit.getHighlightFields());
                }
            }
            result.setHighlights(highlights);
        }

        // 聚合
        if (!pageQuery.getAggregations().isEmpty()) {
            Map<String, Object> aggs = new HashMap<>();
            var container = hits.getAggregations();
            if (container instanceof ElasticsearchAggregations esAggs) {
                for (var entry : esAggs.aggregationsAsMap().entrySet()) {
                    aggs.put(entry.getKey(), entry.getValue().aggregation().getAggregate().toString());
                }
            }
            result.setAggregations(aggs);
        }

        return result;
    }

    private <T> String getIndexName(Class<T> clazz) {
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
