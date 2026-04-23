package com.sloth.boot.starter.ai.support;

import java.util.Map;

/**
 * AI 提示词模板工具类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class AiPromptTemplate {

    /**
     * 模板占位符前缀。
     */
    private static final String PLACEHOLDER_PREFIX = "{{";

    /**
     * 模板占位符后缀。
     */
    private static final String PLACEHOLDER_SUFFIX = "}}";

    private AiPromptTemplate() {
    }

    /**
     * 渲染提示词模板。
     *
     * @param template 模板文本
     * @param variables 变量列表
     * @return 渲染后的文本
     */
    public static String render(String template, Map<String, ?> variables) {
        if (template == null || template.isBlank() || variables == null || variables.isEmpty()) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, ?> entry : variables.entrySet()) {
            String placeholder = PLACEHOLDER_PREFIX + entry.getKey() + PLACEHOLDER_SUFFIX;
            Object value = entry.getValue();
            result = result.replace(placeholder, value == null ? "" : String.valueOf(value));
        }
        return result;
    }
}
