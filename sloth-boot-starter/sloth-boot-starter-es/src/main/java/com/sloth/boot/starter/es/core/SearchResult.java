package com.sloth.boot.starter.es.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 统一搜索结果。
 *
 * @param <T> 文档类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class SearchResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表。
     */
    private List<T> records;

    /**
     * 总记录数。
     */
    private long total;

    /**
     * 最高分。
     */
    private double maxScore;

    /**
     * 高亮字段集合，key 为文档 ID。
     */
    private Map<String, Map<String, List<String>>> highlights;

    /**
     * 聚合结果。
     */
    private Map<String, Object> aggregations;

    /**
     * 滚动 ID（深分页用）。
     */
    private String scrollId;
}
