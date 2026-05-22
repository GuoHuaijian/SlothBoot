package com.sloth.boot.starter.es.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 高亮配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsHighlightConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 高亮字段列表，为空时对所有搜索字段高亮。
     */
    private List<String> fields;

    /**
     * 高亮前置标签。
     */
    @Builder.Default
    private String preTag = "<em>";

    /**
     * 高亮后置标签。
     */
    @Builder.Default
    private String postTag = "</em>";

    /**
     * 片段大小。
     */
    @Builder.Default
    private int fragmentSize = 100;

    /**
     * 片段数量。
     */
    @Builder.Default
    private int numberOfFragments = 3;

    /**
     * 高亮类型。
     */
    @Builder.Default
    private HighlighterType type = HighlighterType.UNIFIED;

    /**
     * 高亮器类型。
     */
    public enum HighlighterType {
        UNIFIED, PLAIN, FVH
    }
}
