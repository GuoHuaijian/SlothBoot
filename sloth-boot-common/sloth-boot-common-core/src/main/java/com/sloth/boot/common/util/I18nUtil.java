package com.sloth.boot.common.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * 国际化工具类。
 * <p>
 * 支持从 {@code i18n/messages} 资源文件中读取国际化消息。
 * 若未注入 Spring {@link MessageSource}，则使用默认的 ResourceBundleMessageSource。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class I18nUtil {

    private static volatile MessageSource messageSource;

    private I18nUtil() {
    }

    /**
     * 注入 Spring MessageSource（由自动配置调用）。
     *
     * @param source 消息源
     */
    public static void setMessageSource(MessageSource source) {
        messageSource = source;
    }

    /**
     * 获取国际化消息。
     *
     * @param key  消息键
     * @param args 参数
     * @return 国际化消息，若未找到则返回 key 本身
     */
    public static String getMessage(String key, Object... args) {
        MessageSource source = messageSource;
        if (source == null) {
            source = getDefaultMessageSource();
            messageSource = source;
        }
        Locale locale = LocaleContextHolder.getLocale();
        return source.getMessage(key, args, key, locale);
    }

    /**
     * 获取当前 Locale 对应的国际化消息。
     *
     * @param key    消息键
     * @param locale 区域
     * @param args   参数
     * @return 国际化消息
     */
    public static String getMessage(String key, Locale locale, Object... args) {
        MessageSource source = messageSource;
        if (source == null) {
            source = getDefaultMessageSource();
            messageSource = source;
        }
        return source.getMessage(key, args, key, locale);
    }

    private static MessageSource getDefaultMessageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("i18n/messages");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }
}
