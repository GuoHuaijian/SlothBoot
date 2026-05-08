package com.sloth.boot.starter.ai.core;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.starter.ai.dto.ImageRequest;
import com.sloth.boot.starter.ai.dto.ImageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("SpringAiImageClient 单元测试")
class SpringAiImageClientTest {

    private ImageModel imageModel;
    private SpringAiImageClient imageClient;

    @BeforeEach
    void setUp() {
        imageModel = mock(ImageModel.class);
        imageClient = new SpringAiImageClient(imageModel);
    }

    @Test
    @DisplayName("简单生成 - 正常调用返回 URL")
    void generate_simplePrompt_returnsUrl() {
        Image image = mock(Image.class);
        when(image.getUrl()).thenReturn("https://example.com/image.png");

        ImageGeneration generation = mock(ImageGeneration.class);
        when(generation.getOutput()).thenReturn(image);

        org.springframework.ai.image.ImageResponse response = mock(org.springframework.ai.image.ImageResponse.class);
        when(response.getResult()).thenReturn(generation);
        when(imageModel.call(any(ImagePrompt.class))).thenReturn(response);

        String url = imageClient.generate("一只猫");

        assertThat(url).isEqualTo("https://example.com/image.png");
    }

    @Test
    @DisplayName("简单生成 - 空提示词抛出异常")
    void generate_emptyPrompt_throwsException() {
        assertThatThrownBy(() -> imageClient.generate(""))
            .isInstanceOf(BizException.class)
            .hasMessageContaining("提示词不能为空");
    }

    @Test
    @DisplayName("请求生成 - 返回 ImageResponse DTO")
    void generate_withRequest_returnsImageResponse() {
        Image image = mock(Image.class);
        when(image.getUrl()).thenReturn("https://example.com/img1.png");

        ImageGeneration generation = mock(ImageGeneration.class);
        when(generation.getOutput()).thenReturn(image);

        org.springframework.ai.image.ImageResponse response = mock(org.springframework.ai.image.ImageResponse.class);
        when(response.getResults()).thenReturn(List.of(generation));
        when(imageModel.call(any(ImagePrompt.class))).thenReturn(response);

        ImageRequest request = ImageRequest.builder()
            .prompt("一只狗")
            .width(512)
            .height(512)
            .build();
        ImageResponse result = imageClient.generate(request);

        assertThat(result.getUrls()).containsExactly("https://example.com/img1.png");
    }

    @Test
    @DisplayName("请求生成 - 空请求抛出异常")
    void generate_nullRequest_throwsException() {
        assertThatThrownBy(() -> imageClient.generate((ImageRequest) null))
            .isInstanceOf(BizException.class);
    }

    @Test
    @DisplayName("模型异常包装为 IMAGE_GENERATION_ERROR")
    void generate_modelException_wrapsAsImageGenerationError() {
        when(imageModel.call(any(ImagePrompt.class))).thenThrow(new RuntimeException("模型错误"));

        assertThatThrownBy(() -> imageClient.generate("测试"))
            .isInstanceOf(BizException.class)
            .hasMessageContaining("图像生成");
    }
}
