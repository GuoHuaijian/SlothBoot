package com.sloth.boot.common.util;

import com.sloth.boot.common.exception.SystemException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 反射工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取方法上的注解（不支持元注解搜索）
     *
     * @param method         方法
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return 注解实例，未找到返回 null
     */
    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        if (method == null || annotationType == null) {
            return null;
        }
        return method.getAnnotation(annotationType);
    }

    /**
     * 获取类上的注解
     *
     * @param clazz          类
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return 注解实例，未找到返回 null
     */
    public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationType) {
        if (clazz == null || annotationType == null) {
            return null;
        }
        return clazz.getAnnotation(annotationType);
    }

    /**
     * 反射获取字段值
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || fieldName == null) {
            return null;
        }
        try {
            Field field = findField(obj.getClass(), fieldName);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw SystemException.of("Reflect get field value failed: " + fieldName, e);
        }
    }

    /**
     * 反射设置字段值
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @param value     值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        if (obj == null || fieldName == null) {
            return;
        }
        try {
            Field field = findField(obj.getClass(), fieldName);
            if (field == null) {
                return;
            }
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw SystemException.of("Reflect set field value failed: " + fieldName, e);
        }
    }

    /**
     * 反射调用方法
     *
     * @param obj            对象
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param args           参数值
     * @return 方法返回值
     */
    public static Object invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
        if (obj == null || methodName == null) {
            return null;
        }
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw SystemException.of("Reflect invoke method failed: " + methodName, e);
        }
    }

    /**
     * 获取父类泛型参数的实际类型
     *
     * @param clazz 子类
     * @param index 泛型参数索引（从 0 开始）
     * @return 泛型参数的实际类型，无法解析返回 null
     */
    public static Class<?> getSuperClassGenericType(Class<?> clazz, int index) {
        if (clazz == null) {
            return null;
        }
        Type genericSuperclass = clazz.getGenericSuperclass();
        while (!(genericSuperclass instanceof ParameterizedType)) {
            if (clazz.getSuperclass() == null || clazz.getSuperclass() == Object.class) {
                return null;
            }
            clazz = clazz.getSuperclass();
            genericSuperclass = clazz.getGenericSuperclass();
        }
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        if (index < 0 || index >= typeArguments.length) {
            return null;
        }
        Type typeArgument = typeArguments[index];
        if (typeArgument instanceof Class) {
            return (Class<?>) typeArgument;
        }
        return null;
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
