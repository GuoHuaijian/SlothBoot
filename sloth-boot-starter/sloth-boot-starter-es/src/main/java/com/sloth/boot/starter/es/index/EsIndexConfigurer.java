package com.sloth.boot.starter.es.index;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * ES 索引配置构建器。
 * <p>
 * 用于定义索引的 settings（分片数、副本数、分词器等）和 mapping （JSON 格式），
 * 配合 {@link com.sloth.boot.starter.es.core.EsTemplate} 创建索引时使用。
 * <pre>{@code
 * EsIndexConfigurer config = EsIndexConfigurer.builder()
 *     .shards(3)
 *     .replicas(1)
 *     .mappingJson("{\"properties\":{...}}")
 *     .build();
 * }</pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@Builder
public class EsIndexConfigurer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主分片数。
     */
    @Builder.Default
    private int shards = 1;

    /**
     * 副本分片数。
     */
    @Builder.Default
    private int replicas = 0;

    /**
     * 分词器（analysis）JSON 配置。
     */
    private String analysisJson;

    /**
     * Mapping JSON 配置。
     */
    private String mappingJson;

    /**
     * 构建 settings JSON。
     *
     * @return settings JSON 字符串，允许为 null
     */
    public String toSettingsJson() {
        if (analysisJson == null && shards == 1 && replicas == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"index\":{")
            .append("\"number_of_shards\":").append(shards).append(",")
            .append("\"number_of_replicas\":").append(replicas)
            .append("}");
        if (analysisJson != null && !analysisJson.isEmpty()) {
            sb.append(",\"analysis\":").append(analysisJson);
        }
        sb.append("}");
        return sb.toString();
    }
}
