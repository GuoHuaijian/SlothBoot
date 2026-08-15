package com.sloth.boot.common.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("I18nMessages 国际化消息测试")
class I18nMessagesTest {

    private Locale originalLocale;

    @BeforeEach
    void setUp() {
        originalLocale = Locale.getDefault();
    }

    @AfterEach
    void tearDown() {
        Locale.setDefault(originalLocale);
    }

    @Test
    @DisplayName("中文默认语言环境返回中文消息")
    void getMessage_returnsChineseWhenDefaultLocaleIsZh() {
        Locale.setDefault(Locale.CHINA);
        assertThat(I18nMessages.getMessage("sloth.success")).isEqualTo("操作成功");
    }

    @Test
    @DisplayName("英文语言环境返回英文消息")
    void getMessage_returnsEnglishWhenDefaultLocaleIsEn() {
        Locale.setDefault(Locale.US);
        assertThat(I18nMessages.getMessage("sloth.success")).isEqualTo("Success");
    }

    @Test
    @DisplayName("带 {0} 占位符的消息正确格式化")
    void getMessage_formatsPlaceholder() {
        Locale.setDefault(Locale.CHINA);
        assertThat(I18nMessages.getMessage("sloth.error.missing_param", "userId")).isEqualTo("缺少请求参数: userId");
    }

    @Test
    @DisplayName("占位符消息不带参数时原样返回")
    void getMessage_withoutArgsReturnsPattern() {
        Locale.setDefault(Locale.CHINA);
        assertThat(I18nMessages.getMessage("sloth.error.missing_param")).isEqualTo("缺少请求参数: {0}");
    }

    @Test
    @DisplayName("缺失 key 回退返回 key 本身")
    void getMessage_missingKeyReturnsKey() {
        Locale.setDefault(Locale.CHINA);
        assertThat(I18nMessages.getMessage("sloth.error.not_exist")).isEqualTo("sloth.error.not_exist");
    }
}
