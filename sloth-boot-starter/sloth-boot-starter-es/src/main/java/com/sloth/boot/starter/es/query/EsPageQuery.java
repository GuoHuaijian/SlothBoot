package com.sloth.boot.starter.es.query;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.starter.es.core.EsTemplate;
import com.sloth.boot.starter.es.core.SearchResult;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 复合搜索请求。
 * <p>
 * 封装查询条件、过滤、排序、高亮、聚合、分页参数，
 * 通过 {@link #execute(EsTemplate, Class)} 直接执行。
 *
 * @param <T> 文档类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class EsPageQuery<T> {

    private EsQueryBuilder query;
    private EsQueryBuilder filter;
    private int pageNum = 1;
    private int pageSize = 10;
    private final List<SortConfig> sorts = new ArrayList<>();
    private EsHighlightConfig highlight;
    private final Map<String, Aggregation> aggregations = new LinkedHashMap<>();
    private List<String> sourceIncludes;
    private List<String> sourceExcludes;

    // ========== 链式方法 ==========

    public EsPageQuery<T> query(EsQueryBuilder query) {
        this.query = query;
        return this;
    }

    public EsPageQuery<T> filter(EsQueryBuilder filter) {
        this.filter = filter;
        return this;
    }

    public EsPageQuery<T> page(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        return this;
    }

    public EsPageQuery<T> sortAsc(String field) {
        sorts.add(new SortConfig(field, SortOrder.Asc));
        return this;
    }

    public EsPageQuery<T> sortDesc(String field) {
        sorts.add(new SortConfig(field, SortOrder.Desc));
        return this;
    }

    public EsPageQuery<T> highlight(EsHighlightConfig config) {
        this.highlight = config;
        return this;
    }

    /**
     * 添加聚合。
     *
     * @param name       聚合名称
     * @param aggregation ES 聚合定义
     */
    public EsPageQuery<T> aggregate(String name, Aggregation aggregation) {
        this.aggregations.put(name, aggregation);
        return this;
    }

    public EsPageQuery<T> includeSources(String... fields) {
        this.sourceIncludes = List.of(fields);
        return this;
    }

    public EsPageQuery<T> excludeSources(String... fields) {
        this.sourceExcludes = List.of(fields);
        return this;
    }

    // ========== 执行 ==========

    /**
     * 执行搜索并返回分页结果。
     *
     * @param template EsTemplate
     * @param clazz    文档类型
     * @return 统一分页结果
     */
    public PageResult<T> execute(EsTemplate template, Class<T> clazz) {
        return template.pageQuery(this, clazz);
    }

    /**
     * 执行搜索并返回完整搜索结果（含高亮、聚合）。
     *
     * @param template EsTemplate
     * @param clazz    文档类型
     * @return 完整搜索结果
     */
    public SearchResult<T> executeFull(EsTemplate template, Class<T> clazz) {
        return template.search(this, clazz);
    }

    /**
     * 执行搜索并返回完整搜索结果。
     *
     * @param template EsTemplate
     * @param clazz    文档类型
     * @param index    索引名
     * @return 完整搜索结果
     */
    public SearchResult<T> executeFull(EsTemplate template, Class<T> clazz, String index) {
        return template.search(this, clazz, index);
    }

    // ========== 内部类 ==========

    @Getter
    public static class SortConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String field;
        private final SortOrder order;

        public SortConfig(String field, SortOrder order) {
            this.field = field;
            this.order = order;
        }
    }
}
