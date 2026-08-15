package com.sloth.boot.common.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 国际化消息工具类。
 * <p>
 * 框架中立实现：直接通过 JDK {@link ResourceBundle} 读取 {@code i18n/messages} 资源文件，
 * 不依赖任何框架。消息未找到时返回 key 本身，与 {@code useCodeAsDefaultMessage} 语义一致。
 * <p>
 * 注意：使用 {@link Locale#getDefault()} 作为语言环境，如需按请求语言动态切换，
 * 请通过应用自身的 MessageSource 处理。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class I18nMessages {

    private static final String BUNDLE_BASE_NAME = "i18n.messages";

    private I18nMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取国际化消息。
     *
     * @param key  消息键
     * @param args 格式化参数
     * @return 国际化消息，未找到时返回 key
     */
    public static String getMessage(String key, Object... args) {
        String pattern = getPattern(key);
        if (args == null || args.length == 0) {
            return pattern;
        }
        return MessageFormat.format(pattern, args);
    }

    private static String getPattern(String key) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.getDefault());
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
