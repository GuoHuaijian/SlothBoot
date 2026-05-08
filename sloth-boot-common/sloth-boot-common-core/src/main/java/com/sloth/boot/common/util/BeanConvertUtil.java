package com.sloth.boot.common.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Bean 属性复制工具类
 * <p>
 * 底层使用 Spring 的 {@link BeanUtils}，支持属性复制、忽略 null、忽略指定字段等场景。
 * <p>
 * 使用示例：
 * <pre>
 * // 基本属性复制
 * UserDTO dto = BeanConvertUtil.convert(user, UserDTO.class);
 *
 * // 复制时忽略 null 值
 * BeanConvertUtil.copyPropertiesIgnoreNull(updateDTO, entity);
 *
 * // 批量转换
 * List&lt;UserDTO&gt; dtoList = BeanConvertUtil.convertList(userList, UserDTO.class);
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class BeanConvertUtil {

    private BeanConvertUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 属性复制
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 属性复制（忽略指定字段）
     *
     * @param source       源对象
     * @param target       目标对象
     * @param ignoreFields 需要忽略的字段名
     */
    public static void copyProperties(Object source, Object target, String... ignoreFields) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target, ignoreFields);
    }

    /**
     * 仅复制非 null 的属性
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        // 收集 source 中为 null 的字段名，作为忽略字段
        Set<String> nullFields = new HashSet<>();
        java.lang.reflect.Field[] fields = source.getClass().getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(source) == null) {
                    nullFields.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                // 忽略无法访问的字段
            }
        }
        BeanUtils.copyProperties(source, target, nullFields.toArray(new String[0]));
    }

    /**
     * 转换对象类型
     *
     * @param source 源对象
     * @param clazz  目标类型
     * @param <T>    目标类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        try {
            T target = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean 转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量转换列表
     *
     * @param sourceList 源列表
     * @param clazz      目标类型
     * @param <T>        目标类型
     * @return 转换后的列表
     */
    public static <T> List<T> convertList(List<?> sourceList, Class<T> clazz) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            result.add(convert(source, clazz));
        }
        return result;
    }
}
