package com.sloth.boot.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型类型解析工具类
 * <p>
 * 用于解析泛型父类的实际类型参数。
 * <p>
 * 使用示例：
 * <pre>
 * public class OrderListener extends AbstractMessageListener&lt;OrderMessage&gt; { ... }
 *
 * Class&lt;?&gt; msgType = GenericTypeUtil.resolveTypeArgument(OrderListener.class, 0);
 * // msgType = OrderMessage.class
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class GenericTypeUtil {

    private GenericTypeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 解析泛型父类的第 N 个类型参数
     *
     * @param clazz 子类
     * @param index 泛型参数索引（从 0 开始）
     * @return 类型参数的实际类型，无法解析返回 null
     */
    public static Class<?> resolveTypeArgument(Class<?> clazz, int index) {
        if (clazz == null || index < 0) {
            return null;
        }
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            Type genericSuperclass = currentClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType parameterizedType) {
                Type[] typeArgs = parameterizedType.getActualTypeArguments();
                if (index < typeArgs.length && typeArgs[index] instanceof Class) {
                    return (Class<?>) typeArgs[index];
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    /**
     * 解析泛型父类的所有类型参数
     *
     * @param clazz 子类
     * @return 类型参数数组，无法解析返回空数组
     */
    public static Class<?>[] resolveTypeArguments(Class<?> clazz) {
        if (clazz == null) {
            return new Class<?>[0];
        }
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            Type genericSuperclass = currentClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType parameterizedType) {
                Type[] typeArgs = parameterizedType.getActualTypeArguments();
                Class<?>[] result = new Class<?>[typeArgs.length];
                for (int i = 0; i < typeArgs.length; i++) {
                    if (typeArgs[i] instanceof Class) {
                        result[i] = (Class<?>) typeArgs[i];
                    } else {
                        result[i] = null;
                    }
                }
                return result;
            }
            currentClass = currentClass.getSuperclass();
        }
        return new Class<?>[0];
    }
}
