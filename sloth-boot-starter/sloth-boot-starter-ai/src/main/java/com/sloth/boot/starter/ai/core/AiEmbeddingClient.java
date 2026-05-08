package com.sloth.boot.starter.ai.core;

import java.util.List;

/**
 * 统一 AI 向量嵌入客户端。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface AiEmbeddingClient {

    /**
     * 生成单条文本的向量嵌入。
     *
     * @param text 输入文本
     * @return 嵌入向量
     */
    float[] embed(String text);

    /**
     * 批量生成文本的向量嵌入。
     *
     * @param texts 输入文本列表
     * @return 嵌入向量列表（与输入顺序一一对应）
     */
    List<float[]> embed(List<String> texts);
}
