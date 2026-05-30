package com.sloth.boot.common.exception;

/**
 * 错误码接口
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface ErrorCode {

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    int getCode();

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    String getMsg();

    /**
     * 获取国际化消息键，用于从 MessageSource 解析国际化消息。
     * <p>
     * 返回 null 表示该错误码不支持国际化，将直接使用 {@link #getMsg()}。
     *
     * @return 国际化消息键，或 null
     */
    default String getI18nKey() {
        return null;
    }
}
