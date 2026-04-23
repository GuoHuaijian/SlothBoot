package com.sloth.boot.starter.ai.support;

import com.sloth.boot.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AI 模块错误码。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum AiErrorCode implements ErrorCode {

    /**
     * 用户提示词为空。
     */
    EMPTY_PROMPT(1900, "AI 提示词不能为空");

    private final int code;

    private final String msg;
}
