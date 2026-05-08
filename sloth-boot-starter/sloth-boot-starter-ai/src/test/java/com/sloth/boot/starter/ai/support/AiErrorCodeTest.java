package com.sloth.boot.starter.ai.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AiErrorCode 单元测试")
class AiErrorCodeTest {

    @Test
    @DisplayName("所有错误码在 1900-1999 范围内")
    void allCodes_inValidRange() {
        for (AiErrorCode errorCode : AiErrorCode.values()) {
            assertThat(errorCode.getCode())
                .isGreaterThanOrEqualTo(1900)
                .isLessThan(2000);
        }
    }

    @Test
    @DisplayName("所有错误码唯一")
    void allCodes_areUnique() {
        Set<Integer> codes = java.util.Arrays.stream(AiErrorCode.values())
            .map(AiErrorCode::getCode)
            .collect(Collectors.toSet());
        assertThat(codes).hasSameSizeAs(AiErrorCode.values());
    }

    @Test
    @DisplayName("所有错误消息非空")
    void allMessages_areNotBlank() {
        for (AiErrorCode errorCode : AiErrorCode.values()) {
            assertThat(errorCode.getMsg()).isNotBlank();
        }
    }

    @Test
    @DisplayName("EMPTY_PROMPT 错误码为 1900")
    void emptyPrompt_code() {
        assertThat(AiErrorCode.EMPTY_PROMPT.getCode()).isEqualTo(1900);
    }

    @Test
    @DisplayName("错误码数量正确（9 个）")
    void errorCodeCount() {
        assertThat(AiErrorCode.values()).hasSize(9);
    }
}
