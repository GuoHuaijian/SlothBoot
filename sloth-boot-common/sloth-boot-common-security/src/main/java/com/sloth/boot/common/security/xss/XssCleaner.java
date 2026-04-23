package com.sloth.boot.common.security.xss;

/**
 * XSS 清洗器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class XssCleaner {

    private static final String SCRIPT_PATTERN = "(?i)<script[^>]*>([\\s\\S]*?)</script>";
    private static final String STYLE_PATTERN = "(?i)<style[^>]*>([\\s\\S]*?)</style>";
    private static final String EVENT_PATTERN = "(?i)on\\w+\\s*=\\s*(['\"]).*?\\1";
    private static final String JS_PROTOCOL_PATTERN = "(?i)javascript:";
    private static final String HTML_TAG_PATTERN = "<[^>]+>";

    /**
     * 清洗 HTML 内容
     *
     * @param content      待清洗内容
     * @param xssProperties XSS 配置
     * @return 清洗后的内容
     */
    public static String clean(String content, XssProperties xssProperties) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        String cleaned = content;

        // 如果需要进一步清理 JavaScript
        if (xssProperties.isCleanJavaScript()) {
            cleaned = cleaned.replaceAll(SCRIPT_PATTERN, "");
            cleaned = cleaned.replaceAll(JS_PROTOCOL_PATTERN, "");
        }

        // 如果需要进一步清理 CSS
        if (xssProperties.isCleanCss()) {
            cleaned = cleaned.replaceAll(STYLE_PATTERN, "");
            cleaned = cleaned.replaceAll("(?i)style\\s*=", "");
        }

        // 如果需要清理事件属性
        if (xssProperties.isCleanEventAttributes()) {
            cleaned = cleaned.replaceAll(EVENT_PATTERN, "");
        }

        if (xssProperties.isCleanHtml()) {
            cleaned = cleaned.replaceAll("(?i)<iframe[^>]*>([\\s\\S]*?)</iframe>", "");
        }

        return cleaned;
    }

    /**
     * 清洗纯文本内容（移除所有 HTML 标签）
     *
     * @param content 待清洗内容
     * @return 清洗后的内容
     */
    public static String cleanText(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        return content.replaceAll(HTML_TAG_PATTERN, "");
    }
}
