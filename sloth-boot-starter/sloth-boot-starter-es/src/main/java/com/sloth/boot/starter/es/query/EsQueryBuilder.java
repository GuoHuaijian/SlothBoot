package com.sloth.boot.starter.es.query;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Elasticsearch 查询条件构建器。
 * <p>
 * 链式调用，简化 ES 查询 DSL 构建。示例：
 * <pre>{@code
 * EsQueryBuilder q = EsQueryBuilder.bool()
 *     .must(EsQueryBuilder.match("name", "张三"))
 *     .filter(EsQueryBuilder.range("age", 18, 60))
 *     .mustNot(EsQueryBuilder.term("status", "deleted"));
 * }</pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class EsQueryBuilder implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Query query;

    /**
     * 排序条件列表。
     */
    private final List<SortOptions> sortOptions = new ArrayList<>();

    private EsQueryBuilder(Query query) {
        this.query = query;
    }

    // ========== 精确匹配 ==========

    public static EsQueryBuilder term(String field, Object value) {
        return new EsQueryBuilder(QueryBuilders.term(t -> t.field(field).value(toFieldValue(value))));
    }

    public static EsQueryBuilder terms(String field, Collection<?> values) {
        List<FieldValue> fieldValues = values.stream()
            .map(EsQueryBuilder::toFieldValue)
            .collect(Collectors.toList());
        return new EsQueryBuilder(QueryBuilders.terms(t -> t.field(field).terms(ts -> ts.value(fieldValues))));
    }

    public static EsQueryBuilder exists(String field) {
        return new EsQueryBuilder(QueryBuilders.exists(e -> e.field(field)));
    }

    public static EsQueryBuilder ids(String... ids) {
        return new EsQueryBuilder(QueryBuilders.ids(i -> i.values(Arrays.asList(ids))));
    }

    // ========== 全文匹配 ==========

    public static EsQueryBuilder match(String field, String text) {
        return new EsQueryBuilder(QueryBuilders.match(m -> m.field(field).query(text)));
    }

    public static EsQueryBuilder matchPhrase(String field, String text) {
        return new EsQueryBuilder(QueryBuilders.matchPhrase(m -> m.field(field).query(text)));
    }

    public static EsQueryBuilder multiMatch(String text, String... fields) {
        return new EsQueryBuilder(QueryBuilders.multiMatch(m -> m.query(text).fields(Arrays.asList(fields))));
    }

    public static EsQueryBuilder queryString(String query) {
        return new EsQueryBuilder(QueryBuilders.queryString(q -> q.query(query)));
    }

    public static EsQueryBuilder simpleQueryString(String query) {
        return new EsQueryBuilder(QueryBuilders.simpleQueryString(q -> q.query(query)));
    }

    // ========== 范围匹配 ==========

    /**
     * 创建范围查询 (from ≤ field ≤ to)。
     * <p>使用 untyped range，支持任意可比较类型。</p>
     */
    public static EsQueryBuilder range(String field, Object from, Object to) {
        return new EsQueryBuilder(QueryBuilders.range(r -> r.untyped(u -> u
            .field(field)
            .gte(JsonData.of(from))
            .lte(JsonData.of(to))
        )));
    }

    public static EsQueryBuilder rangeGt(String field, Object value) {
        return new EsQueryBuilder(QueryBuilders.range(r -> r.untyped(u -> u
            .field(field).gt(JsonData.of(value))
        )));
    }

    public static EsQueryBuilder rangeGte(String field, Object value) {
        return new EsQueryBuilder(QueryBuilders.range(r -> r.untyped(u -> u
            .field(field).gte(JsonData.of(value))
        )));
    }

    public static EsQueryBuilder rangeLt(String field, Object value) {
        return new EsQueryBuilder(QueryBuilders.range(r -> r.untyped(u -> u
            .field(field).lt(JsonData.of(value))
        )));
    }

    public static EsQueryBuilder rangeLte(String field, Object value) {
        return new EsQueryBuilder(QueryBuilders.range(r -> r.untyped(u -> u
            .field(field).lte(JsonData.of(value))
        )));
    }

    // ========== 模糊/通配 ==========

    public static EsQueryBuilder prefix(String field, String value) {
        return new EsQueryBuilder(QueryBuilders.prefix(p -> p.field(field).value(value)));
    }

    public static EsQueryBuilder wildcard(String field, String pattern) {
        return new EsQueryBuilder(QueryBuilders.wildcard(w -> w.field(field).wildcard(pattern)));
    }

    public static EsQueryBuilder fuzzy(String field, String value) {
        return new EsQueryBuilder(QueryBuilders.fuzzy(f -> f.field(field).value(value)));
    }

    public static EsQueryBuilder regexp(String field, String pattern) {
        return new EsQueryBuilder(QueryBuilders.regexp(r -> r.field(field).value(pattern)));
    }

    // ========== 地理位置 ==========

    /**
     * 地理距离查询。
     *
     * @param field    地理坐标字段名
     * @param lat      纬度
     * @param lon      经度
     * @param distance 距离，如 "10km"
     */
    public static EsQueryBuilder geoDistance(String field, double lat, double lon, String distance) {
        return new EsQueryBuilder(QueryBuilders.geoDistance(g -> g
            .field(field)
            .location(gl -> gl.latlon(ll -> ll.lat(lat).lon(lon)))
            .distance(distance)));
    }

    /**
     * 地理边界框查询。
     *
     * @param field    地理坐标字段名
     * @param topLat   左上角纬度
     * @param leftLon  左上角经度
     * @param bottomLat 右下角纬度
     * @param rightLon  右下角经度
     */
    public static EsQueryBuilder geoBoundingBox(String field, double topLat, double leftLon,
                                                 double bottomLat, double rightLon) {
        return new EsQueryBuilder(QueryBuilders.geoBoundingBox(g -> g
            .field(field)
            .boundingBox(bb -> bb.tlbr(t -> t
                .topLeft(gl -> gl.latlon(ll -> ll.lat(topLat).lon(leftLon)))
                .bottomRight(gl -> gl.latlon(ll -> ll.lat(bottomLat).lon(rightLon)))
            ))));
    }

    // ========== 复合查询 ==========

    public static EsBoolQueryBuilder bool() {
        return new EsBoolQueryBuilder();
    }

    public static EsQueryBuilder nested(String path, EsQueryBuilder innerQuery) {
        return new EsQueryBuilder(QueryBuilders.nested(n -> n.path(path).query(innerQuery.query)));
    }

    public EsQueryBuilder constantScore(float boost) {
        return new EsQueryBuilder(QueryBuilders.constantScore(c -> c
            .filter(query)
            .boost(boost)));
    }

    // ========== 排序 ==========

    public EsQueryBuilder sortAsc(String field) {
        sortOptions.add(SortOptions.of(s -> s.field(f -> f.field(field).order(SortOrder.Asc))));
        return this;
    }

    public EsQueryBuilder sortDesc(String field) {
        sortOptions.add(SortOptions.of(s -> s.field(f -> f.field(field).order(SortOrder.Desc))));
        return this;
    }

    // ========== 工具方法 ==========

    private static FieldValue toFieldValue(Object value) {
        if (value instanceof String s) {
            return FieldValue.of(s);
        } else if (value instanceof Long l) {
            return FieldValue.of(l);
        } else if (value instanceof Integer i) {
            return FieldValue.of(i.longValue());
        } else if (value instanceof Double d) {
            return FieldValue.of(d);
        } else if (value instanceof Float f) {
            return FieldValue.of(f.doubleValue());
        } else if (value instanceof Boolean b) {
            return FieldValue.of(b);
        }
        return FieldValue.of(String.valueOf(value));
    }

    // ========== Bool 查询构建器 ==========

    public static class EsBoolQueryBuilder {
        private final List<Query> must = new ArrayList<>();
        private final List<Query> should = new ArrayList<>();
        private final List<Query> filter = new ArrayList<>();
        private final List<Query> mustNot = new ArrayList<>();
        private float boost = 1.0f;

        public EsBoolQueryBuilder must(EsQueryBuilder... queries) {
            for (EsQueryBuilder q : queries) {
                this.must.add(q.query);
            }
            return this;
        }

        public EsBoolQueryBuilder should(EsQueryBuilder... queries) {
            for (EsQueryBuilder q : queries) {
                this.should.add(q.query);
            }
            return this;
        }

        public EsBoolQueryBuilder filter(EsQueryBuilder... queries) {
            for (EsQueryBuilder q : queries) {
                this.filter.add(q.query);
            }
            return this;
        }

        public EsBoolQueryBuilder mustNot(EsQueryBuilder... queries) {
            for (EsQueryBuilder q : queries) {
                this.mustNot.add(q.query);
            }
            return this;
        }

        public EsBoolQueryBuilder boost(float boost) {
            this.boost = boost;
            return this;
        }

        public EsQueryBuilder build() {
            BoolQuery boolQuery = BoolQuery.of(b -> {
                if (!must.isEmpty()) {
                    b.must(must);
                }
                if (!should.isEmpty()) {
                    b.should(should);
                }
                if (!filter.isEmpty()) {
                    b.filter(filter);
                }
                if (!mustNot.isEmpty()) {
                    b.mustNot(mustNot);
                }
                b.boost(boost);
                return b;
            });
            return new EsQueryBuilder(boolQuery._toQuery());
        }
    }
}
