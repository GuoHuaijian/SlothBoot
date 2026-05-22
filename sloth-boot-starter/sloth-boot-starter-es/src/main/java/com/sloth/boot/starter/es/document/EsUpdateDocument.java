package com.sloth.boot.starter.es.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ES 文档局部更新请求。
 * <p>
 * 支持字段级更新、脚本更新和 upsert 操作。
 * 配合 {@link com.sloth.boot.starter.es.core.EsTemplate#updatePartial(String, EsUpdateDocument, Class)} 使用。
 * <pre>{@code
 * EsUpdateDocument update = EsUpdateDocument.builder()
 *     .field("name", "新名称")
 *     .field("price", 99.9)
 *     .retryOnConflict(5)
 *     .build();
 * esTemplate.updatePartial("doc-id", update, Product.class);
 * }</pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@Builder
public class EsUpdateDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 待更新的字段。
     */
    @Singular("field")
    private Map<String, Object> fields;

    /**
     * 脚本内容（ELK Painless 脚本），与 fields 互斥。
     */
    private String script;

    /**
     * 脚本参数。
     */
    private Map<String, Object> scriptParams;

    /**
     * 更新冲突重试次数，为空时使用全局默认值。
     */
    private Integer retryOnConflict;

    /**
     * 文档不存在时是否插入。
     */
    private boolean upsert;

    /**
     * 检测 fields 是否为空。
     *
     * @return true 表示无字段更新
     */
    public boolean hasFields() {
        return fields != null && !fields.isEmpty();
    }

    /**
     * 检测是否有脚本更新。
     *
     * @return true 表示为脚本更新
     */
    public boolean hasScript() {
        return script != null && !script.isEmpty();
    }

    /**
     * 获取非空更新的字段 Map。
     *
     * @return 字段 Map
     */
    public Map<String, Object> getFields() {
        return fields != null ? fields : new HashMap<>();
    }

    /**
     * 获取非空脚本参数 Map。
     *
     * @return 脚本参数
     */
    public Map<String, Object> getScriptParams() {
        return scriptParams != null ? scriptParams : new HashMap<>();
    }
}
