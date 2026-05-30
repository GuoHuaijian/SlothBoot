package com.sloth.boot.common.security.xss;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * XSS 清洗器测试。
 */
class XssCleanerTest {

    private final XssProperties properties = new XssProperties();

    @Test
    void should_clean_script_tags() {
        String input = "<script>alert('xss')</script>Hello";
        assertThat(XssCleaner.clean(input, properties)).isEqualTo("Hello");
    }

    @Test
    void should_clean_event_attributes_with_quotes() {
        String input = "<img src=x onerror=\"alert(1)\">";
        String cleaned = XssCleaner.clean(input, properties);
        assertThat(cleaned).doesNotContain("onerror");
    }

    @Test
    void should_clean_event_attributes_without_quotes() {
        String input = "<img src=x onerror=alert(1)>";
        String cleaned = XssCleaner.clean(input, properties);
        assertThat(cleaned).doesNotContain("onerror");
    }

    @Test
    void should_clean_javascript_protocol() {
        String input = "<a href=\"javascript:alert(1)\">click</a>";
        String cleaned = XssCleaner.clean(input, properties);
        assertThat(cleaned).doesNotContain("javascript:");
    }

    @Test
    void should_clean_javascript_with_space() {
        String input = "<a href=\"javascript :alert(1)\">click</a>";
        String cleaned = XssCleaner.clean(input, properties);
        assertThat(cleaned).doesNotContain("javascript");
    }

    @Test
    void should_clean_style_tags() {
        String input = "<style>body{background:red}</style>Hello";
        assertThat(XssCleaner.clean(input, properties)).isEqualTo("Hello");
    }

    @Test
    void should_clean_iframe_tags() {
        String input = "<iframe src=evil></iframe>Hello";
        assertThat(XssCleaner.clean(input, properties)).isEqualTo("Hello");
    }

    @Test
    void should_handle_null_input() {
        assertThat(XssCleaner.clean(null, properties)).isNull();
    }

    @Test
    void should_handle_empty_input() {
        assertThat(XssCleaner.clean("", properties)).isEmpty();
    }

    @Test
    void should_clean_text_of_all_html_tags() {
        String input = "<b>Bold</b> and <i>italic</i>";
        assertThat(XssCleaner.cleanText(input)).isEqualTo("Bold and italic");
    }

    @Test
    void should_respect_property_toggles() {
        XssProperties props = new XssProperties();
        props.setCleanJavaScript(false);
        String input = "<script>alert(1)</script>Hello";
        assertThat(XssCleaner.clean(input, props)).contains("alert(1)");
    }
}
