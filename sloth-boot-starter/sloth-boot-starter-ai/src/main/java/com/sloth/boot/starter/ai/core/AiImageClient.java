package com.sloth.boot.starter.ai.core;

import com.sloth.boot.starter.ai.dto.ImageRequest;
import com.sloth.boot.starter.ai.dto.ImageResponse;

/**
 * 统一 AI 图像生成客户端。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface AiImageClient {

    /**
     * 根据文本提示生成图像，返回图像 URL。
     *
     * @param prompt 图像描述
     * @return 图像 URL
     */
    String generate(String prompt);

    /**
     * 根据请求参数生成图像（支持自定义尺寸、数量等）。
     *
     * @param request 图像生成请求
     * @return 图像生成响应
     */
    ImageResponse generate(ImageRequest request);
}
