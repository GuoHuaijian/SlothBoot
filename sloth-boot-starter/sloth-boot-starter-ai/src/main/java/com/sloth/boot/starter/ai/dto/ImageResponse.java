package com.sloth.boot.starter.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * AI 图像生成响应。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
public class ImageResponse {

    /**
     * 图像 URL 列表。
     */
    private List<String> urls;

    /**
     * 修订后的提示词（部分模型提供）。
     */
    private String revisedPrompt;
}
