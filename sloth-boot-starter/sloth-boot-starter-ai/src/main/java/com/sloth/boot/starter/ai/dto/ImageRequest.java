package com.sloth.boot.starter.ai.dto;

import lombok.Builder;
import lombok.Data;

/**
 * AI 图像生成请求。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
public class ImageRequest {

    /**
     * 图像描述提示词。
     */
    private String prompt;

    /**
     * 图像宽度。
     */
    private Integer width;

    /**
     * 图像高度。
     */
    private Integer height;

    /**
     * 模型名称（可选）。
     */
    private String model;

    /**
     * 生成数量（可选，默认 1）。
     */
    @Builder.Default
    private Integer n = 1;
}
