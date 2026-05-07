package com.sloth.boot.common.config;

import com.sloth.boot.common.util.I18nUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 国际化自动配置。
 * <p>
 * 注册 MessageSource Bean 并注入到 {@link I18nUtil}。
 * 消息文件位于 classpath:i18n/messages.properties。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.context.MessageSource")
public class I18nAutoConfiguration {

    @Bean
    public MessageSource slothMessageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("i18n/messages");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(true);
        I18nUtil.setMessageSource(source);
        return source;
    }
}
