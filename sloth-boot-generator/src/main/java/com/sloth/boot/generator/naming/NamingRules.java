package com.sloth.boot.generator.naming;

import java.util.Arrays;

/**
 * 命名转换规则：表名/列名与 Java 类名/字段名之间的映射。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class NamingRules {

    private NamingRules() {
    }

    /**
     * 表名转类名：移除前缀、下划线转驼峰、首字母大写。
     * <p>
     * 如 {@code sys_user_role} + 前缀 {@code sys_} → {@code UserRole}。
     *
     * @param tableName     表名
     * @param tablePrefixes 需要移除的表名前缀
     * @return 类名
     */
    public static String toClassName(String tableName, String[] tablePrefixes) {
        String name = stripPrefix(tableName, tablePrefixes);
        return capitalize(toCamelCase(name));
    }

    /**
     * 列名转字段名：下划线转驼峰、首字母小写。
     * <p>
     * 如 {@code user_name} → {@code userName}。
     *
     * @param columnName 列名
     * @return 字段名
     */
    public static String toFieldName(String columnName) {
        return uncapitalize(toCamelCase(columnName));
    }

    /**
     * 类名转 REST 资源路径段：小写连字符 + 简单复数化。
     * <p>
     * 如 {@code SysUser} → {@code users}，{@code Category} → {@code categories}，
     * {@code Box} → {@code boxes}。仅覆盖常见复数规则，特殊名词可通过配置显式指定。
     *
     * @param className 类名
     * @return 资源路径段
     */
    public static String toResourceSegment(String className) {
        String noun = uncapitalize(className);
        return pluralize(noun);
    }

    /**
     * 类名转变量名（首字母小写）。
     *
     * @param className 类名
     * @return 变量名
     */
    public static String toVariableName(String className) {
        return uncapitalize(className);
    }

    /**
     * 移除表名前缀。
     */
    public static String stripPrefix(String tableName, String[] tablePrefixes) {
        if (tablePrefixes == null || tablePrefixes.length == 0) {
            return tableName;
        }
        return Arrays.stream(tablePrefixes)
            .filter(prefix -> !prefix.isEmpty() && tableName.startsWith(prefix))
            .findFirst()
            .map(prefix -> tableName.substring(prefix.length()))
            .orElse(tableName);
    }

    /**
     * 下划线风格转驼峰风格。
     */
    public static String toCamelCase(String underscoreName) {
        StringBuilder builder = new StringBuilder(underscoreName.length());
        boolean upperNext = false;
        for (char c : underscoreName.toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else if (upperNext) {
                builder.append(Character.toUpperCase(c));
                upperNext = false;
            } else {
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

    /**
     * 首字母大写。
     */
    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * 首字母小写。
     */
    public static String uncapitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    private static String pluralize(String noun) {
        if (noun.endsWith("y") && !isVowel(noun.charAt(noun.length() - 2))) {
            return noun.substring(0, noun.length() - 1) + "ies";
        }
        if (endsWithAny(noun, "s", "x", "z", "ch", "sh")) {
            return noun + "es";
        }
        return noun + "s";
    }

    private static boolean endsWithAny(String value, String... suffixes) {
        return Arrays.stream(suffixes).anyMatch(value::endsWith);
    }

    private static boolean isVowel(char c) {
        return "aeiou".indexOf(c) >= 0;
    }
}
