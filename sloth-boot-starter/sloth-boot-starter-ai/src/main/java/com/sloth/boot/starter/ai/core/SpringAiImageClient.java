package com.sloth.boot.starter.ai.core;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.starter.ai.dto.ImageRequest;
import com.sloth.boot.starter.ai.dto.ImageResponse;
import com.sloth.boot.starter.ai.support.AiErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 基于 Spring AI ImageModel 的图像生成客户端。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SpringAiImageClient implements AiImageClient {

    private final ImageModel imageModel;

    /**
     * {@inheritDoc}
     */
    @Override
    public String generate(String prompt) {
        if (!StringUtils.hasText(prompt)) {
            throw BizException.of(AiErrorCode.EMPTY_PROMPT);
        }
        try {
            ImagePrompt imagePrompt = new ImagePrompt(prompt);
            org.springframework.ai.image.ImageResponse response = imageModel.call(imagePrompt);
            ImageGeneration generation = response.getResult();
            return generation.getOutput().getUrl();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw BizException.of(AiErrorCode.IMAGE_GENERATION_ERROR);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageResponse generate(ImageRequest request) {
        if (request == null || !StringUtils.hasText(request.getPrompt())) {
            throw BizException.of(AiErrorCode.EMPTY_PROMPT);
        }
        try {
            OpenAiImageOptions.Builder optionsBuilder = OpenAiImageOptions.builder();
            if (StringUtils.hasText(request.getModel())) {
                optionsBuilder.model(request.getModel());
            }
            if (request.getWidth() != null) {
                optionsBuilder.width(request.getWidth());
            }
            if (request.getHeight() != null) {
                optionsBuilder.height(request.getHeight());
            }
            if (request.getN() != null) {
                optionsBuilder.N(request.getN());
            }

            ImagePrompt imagePrompt = new ImagePrompt(request.getPrompt(), optionsBuilder.build());
            org.springframework.ai.image.ImageResponse response = imageModel.call(imagePrompt);

            List<String> urls = response.getResults().stream().map(gen -> gen.getOutput().getUrl()).toList();

            return ImageResponse.builder().urls(urls).build();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw BizException.of(AiErrorCode.IMAGE_GENERATION_ERROR);
        }
    }
}
