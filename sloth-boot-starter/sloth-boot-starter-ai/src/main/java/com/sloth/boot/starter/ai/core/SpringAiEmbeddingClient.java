package com.sloth.boot.starter.ai.core;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.starter.ai.support.AiErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于 Spring AI EmbeddingModel 的向量嵌入客户端。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SpringAiEmbeddingClient implements AiEmbeddingClient {

    private final EmbeddingModel embeddingModel;

    /**
     * {@inheritDoc}
     */
    @Override
    public float[] embed(String text) {
        if (!StringUtils.hasText(text)) {
            throw BizException.of(AiErrorCode.EMPTY_PROMPT);
        }
        try {
            return embeddingModel.embed(text);
        } catch (Exception e) {
            throw BizException.of(AiErrorCode.EMBEDDING_ERROR);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<float[]> embed(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            throw BizException.of(AiErrorCode.EMPTY_PROMPT);
        }
        try {
            List<float[]> results = new ArrayList<>(texts.size());
            for (String text : texts) {
                results.add(embeddingModel.embed(text));
            }
            return results;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw BizException.of(AiErrorCode.EMBEDDING_ERROR);
        }
    }
}
