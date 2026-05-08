package com.sloth.boot.common.util;

import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板渲染工具类
 * <p>
 * 支持 {@code {{key}}} 占位符替换，适用于 AI 提示词模板、短信模板、告警消息等场景。
 * <p>
 * 使用示例：
 * <pre>
 * // 使用 Map 替换
 * Map&lt;String, String&gt; vars = new HashMap&lt;&gt;();
 * vars.put("name", "张三");
 * vars.put("age", "25");
 * String result = TemplateUtil.render("你好，{{name}}，你今年{{age}}岁", vars);
 * // "你好，张三，你今年25岁"
 *
 * // 使用 Bean 属性替换
 * public class User { private String name = "李四"; }
 * String result = TemplateUtil.render("你好，{{name}}", new User());
 * // "你好，李四"
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class TemplateUtil {

    private TemplateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 占位符正则：匹配 {{key}}
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    /**
     * 使用 Map 中的变量替换模板中的 {{key}} 占位符
     *
     * @param template  模板字符串
     * @param variables 变量 Map
     * @return 渲染后的字符串
     */
    public static String render(String template, Map<String, String> variables) {
        if (StrUtil.isBlank(template) || variables == null || variables.isEmpty()) {
            return template;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = variables.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 使用 Bean 属性替换模板中的 {{propertyName}} 占位符
     * <p>
     * 通过 getter 方法获取属性值，支持 camelCase 属性名。
     *
     * @param template 模板字符串
     * @param bean     Bean 对象
     * @return 渲染后的字符串
     */
    public static String render(String template, Object bean) {
        if (StrUtil.isBlank(template) || bean == null) {
            return template;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String propertyName = matcher.group(1);
            String value = getBeanProperty(bean, propertyName);
            if (value == null) {
                value = matcher.group(0);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String getBeanProperty(Object bean, String propertyName) {
        try {
            String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            Method getter = bean.getClass().getMethod(getterName);
            Object value = getter.invoke(bean);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
