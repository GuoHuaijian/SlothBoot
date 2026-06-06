package com.sloth.boot.starter.es.core;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.starter.es.config.EsProperties;
import com.sloth.boot.starter.es.query.EsHighlightConfig;
import com.sloth.boot.starter.es.query.EsPageQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Elasticsearch 搜索模板。
 * <p>
 * 提供原生查询、分页搜索、滚动查询、Search After 深分页等操作。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class EsSearchTemplate {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EsProperties esProperties;


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

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
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

        // 将 EsHighlightConfig 中的高亮参数映射为 Spring Data ES 的 HighlightFieldParameters
        HighlightFieldParameters.HighlightFieldParametersBuilder paramsBuilder =
            HighlightFieldParameters.builder()
                .withPreTags(config.getPreTag())       // 高亮前缀标签，如 <em>
                .withPostTags(config.getPostTag());     // 高亮后缀标签，如 </em>

        if (config.getFragmentSize() > 0) {
            paramsBuilder.withFragmentSize(config.getFragmentSize());         // 每个高亮片段最大字符数
        }
        if (config.getNumberOfFragments() > 0) {
            paramsBuilder.withNumberOfFragments(config.getNumberOfFragments()); // 返回的高亮片段数量
        }
        if (config.getType() != null) {
            paramsBuilder.withType(config.getType().name());                  // 高亮实现类型，如 unified、plain、fvh
        }

        // 为每个字段应用相同的高亮参数
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

        // 高亮结果提取：按文档 ID 收集每个命中的高亮字段
        if (pageQuery.getHighlight() != null) {
            Map<String, Map<String, List<String>>> highlights = new HashMap<>();
            for (SearchHit<T> hit : hits.getSearchHits()) {
                if (!hit.getHighlightFields().isEmpty()) {
                    highlights.put(hit.getId(), hit.getHighlightFields());
                }
            }
            result.setHighlights(highlights);
        }

        // 聚合结果提取：先尝试转为 Spring Data ES 的 ElasticsearchAggregations，
        // 再遍历每个聚合项转为字符串形式的 Map（key=聚合名, value=聚合结果JSON）
        if (!pageQuery.getAggregations().isEmpty()) {
            Map<String, Object> aggs = new HashMap<>();
            AggregationsContainer<?> container = hits.getAggregations();
            if (container instanceof ElasticsearchAggregations esAggs) {
                for (Map.Entry<String, ElasticsearchAggregation> entry : esAggs.aggregationsAsMap().entrySet()) {
                    aggs.put(entry.getKey(), entry.getValue().aggregation().getAggregate().toString());
                }
            }
            result.setAggregations(aggs);
        }

        return result;
    }
}
