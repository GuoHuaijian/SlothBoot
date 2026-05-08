package com.sloth.boot.starter.ai.core;

import com.sloth.boot.common.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("SpringAiEmbeddingClient 单元测试")
class SpringAiEmbeddingClientTest {

    private EmbeddingModel embeddingModel;
    private SpringAiEmbeddingClient embeddingClient;

    @BeforeEach
    void setUp() {
        embeddingModel = mock(EmbeddingModel.class);
        embeddingClient = new SpringAiEmbeddingClient(embeddingModel);
    }

    @Test
    @DisplayName("单条嵌入 - 正常调用")
    void embed_singleText_returnsVector() {
        float[] expected = {0.1f, 0.2f, 0.3f};
        when(embeddingModel.embed(anyString())).thenReturn(expected);

        float[] result = embeddingClient.embed("测试文本");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("单条嵌入 - 空文本抛出异常")
    void embed_emptyText_throwsException() {
        assertThatThrownBy(() -> embeddingClient.embed(""))
            .isInstanceOf(BizException.class)
            .hasMessageContaining("提示词不能为空");
    }

    @Test
    @DisplayName("单条嵌入 - null 文本抛出异常")
    void embed_nullText_throwsException() {
        assertThatThrownBy(() -> embeddingClient.embed((String) null))
            .isInstanceOf(BizException.class);
    }

    @Test
    @DisplayName("单条嵌入 - 模型异常包装为 EMBEDDING_ERROR")
    void embed_modelException_wrapsAsEmbeddingError() {
        when(embeddingModel.embed(anyString())).thenThrow(new RuntimeException("模型错误"));

        assertThatThrownBy(() -> embeddingClient.embed("文本"))
            .isInstanceOf(BizException.class)
            .hasMessageContaining("向量嵌入");
    }
}
