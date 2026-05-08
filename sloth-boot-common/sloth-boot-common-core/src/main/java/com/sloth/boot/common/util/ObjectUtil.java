package com.sloth.boot.common.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 对象工具类
 * <p>
 * 提供常用的对象空安全操作方法。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ObjectUtil {

    private ObjectUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 判断对象是否为 null
     *
     * @param obj 对象
     * @return 是否为 null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断对象是否不为 null
     *
     * @param obj 对象
     * @return 是否不为 null
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 如果对象为 null 则返回默认值
     *
     * @param obj          对象
     * @param defaultValue 默认值
     * @param <T>          对象类型
     * @return 对象本身或默认值
     */
    public static <T> T defaultIfNull(T obj, T defaultValue) {
        return obj != null ? obj : defaultValue;
    }

    /**
     * 返回第一个非 null 的值
     *
     * @param values 值列表
     * @param <T>    值类型
     * @return 第一个非 null 的值，全部为 null 则返回 null
     */
    @SafeVarargs
    public static <T> T firstNonNull(T... values) {
        if (values != null) {
            for (T value : values) {
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * 空安全的 equals 比较
     *
     * @param a 对象a
     * @param b 对象b
     * @return 是否相等
     */
    public static boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    /**
     * 深度相等比较
     *
     * @param a 对象a
     * @param b 对象b
     * @return 是否深度相等
     */
    public static boolean deepEquals(Object a, Object b) {
        return Objects.deepEquals(a, b);
    }

    /**
     * 综合判空
     * <p>
     * 判断对象是否为以下空状态之一：
     * <ul>
     *   <li>null</li>
     *   <li>空字符串 ""</li>
     *   <li>空白字符串</li>
     *   <li>空集合</li>
     *   <li>空 Map</li>
     *   <li>空数组</li>
     * </ul>
     *
     * @param obj 对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).isEmpty();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        return false;
    }

    /**
     * 综合非空判断
     *
     * @param obj 对象
     * @return 是否非空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 空安全的 toString
     *
     * @param obj 对象
     * @return toString 结果，null 返回空字符串
     */
    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 空安全的 toString
     *
     * @param obj          对象
     * @param defaultValue 默认值
     * @return toString 结果，null 返回默认值
     */
    public static String toString(Object obj, String defaultValue) {
        return obj == null ? defaultValue : obj.toString();
    }

    /**
     * 断言对象不为 null
     *
     * @param obj     对象
     * @param message 错误信息
     * @throws IllegalArgumentException 如果对象为 null
     */
    public static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言对象不为 null（延迟构造错误信息）
     *
     * @param obj             对象
     * @param messageSupplier 错误信息供应器
     * @param <T>             对象类型
     * @return 非 null 的对象
     * @throws IllegalArgumentException 如果对象为 null
     */
    public static <T> T requireNonNull(T obj, Supplier<String> messageSupplier) {
        if (obj == null) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return obj;
    }
}
