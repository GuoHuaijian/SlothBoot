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

    /** 查询条件构建器 */
    private EsQueryBuilder query;
    /** 过滤条件构建器 */
    private EsQueryBuilder filter;
    /** 页码，从 1 开始 */
    private int pageNum = 1;
    /** 每页大小 */
    private int pageSize = 10;
    /** 排序配置列表 */
    private final List<SortConfig> sorts = new ArrayList<>();
    /** 高亮配置 */
    private EsHighlightConfig highlight;
    /** 聚合定义映射 */
    private final Map<String, Aggregation> aggregations = new LinkedHashMap<>();
    /** 包含返回的源字段 */
    private List<String> sourceIncludes;
    /** 排除返回的源字段 */
    private List<String> sourceExcludes;

    // ========== 链式方法 ==========

    /**
     * 设置查询条件。
     *
     * @param query 查询条件构建器
     * @return 当前实例
     */
    public EsPageQuery<T> query(EsQueryBuilder query) {
        this.query = query;
        return this;
    }

    /**
     * 设置过滤条件。
     *
     * @param filter 过滤条件构建器
     * @return 当前实例
     */
    public EsPageQuery<T> filter(EsQueryBuilder filter) {
        this.filter = filter;
        return this;
    }

    /**
     * 设置分页参数。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 当前实例
     */
    public EsPageQuery<T> page(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 添加升序排序。
     *
     * @param field 排序字段
     * @return 当前实例
     */
    public EsPageQuery<T> sortAsc(String field) {
        sorts.add(new SortConfig(field, SortOrder.Asc));
        return this;
    }

    /**
     * 添加降序排序。
     *
     * @param field 排序字段
     * @return 当前实例
     */
    public EsPageQuery<T> sortDesc(String field) {
        sorts.add(new SortConfig(field, SortOrder.Desc));
        return this;
    }

    /**
     * 设置高亮配置。
     *
     * @param config 高亮配置
     * @return 当前实例
     */
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

    /**
     * 设置包含返回的源字段。
     *
     * @param fields 源字段列表
     * @return 当前实例
     */
    public EsPageQuery<T> includeSources(String... fields) {
        this.sourceIncludes = List.of(fields);
        return this;
    }

    /**
     * 设置排除返回的源字段。
     *
     * @param fields 源字段列表
     * @return 当前实例
     */
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

    /**
     * 排序配置。
     */
    @Getter
    public static class SortConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 排序字段 */
        private final String field;
        /** 排序方向 */
        private final SortOrder order;

        public SortConfig(String field, SortOrder order) {
            this.field = field;
            this.order = order;
        }
    }
}
