package com.sloth.boot.starter.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "AI 图像生成请求")
public class ImageRequest {

    /**
     * 图像描述提示词。
     */
    @Schema(description = "图像描述提示词", example = "一只在草地上奔跑的金毛犬")
    private String prompt;

    /**
     * 图像宽度。
     */
    @Schema(description = "图像宽度（像素）", example = "1024")
    private Integer width;

    /**
     * 图像高度。
     */
    @Schema(description = "图像高度（像素）", example = "1024")
    private Integer height;

    /**
     * 模型名称（可选）。
     */
    @Schema(description = "模型名称（可选）", example = "dall-e-3")
    private String model;

    /**
     * 生成数量（可选，默认 1）。
     */
    @Schema(description = "生成数量（默认1）", example = "1")
    @Builder.Default
    private Integer n = 1;
}
