package com.sloth.boot.common.util.jackson;

import com.sloth.boot.common.enums.IBaseEnum;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;

/**
 * IBaseEnum 反序列化修饰器
 * <p>
 * 使 {@link IBaseEnumDeserializer} 对实现 {@link IBaseEnum} 的具体枚举类型生效，
 * 否则 Jackson 会使用内置 {@code EnumDeserializer}，无法匹配序列化输出的对象格式。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class IBaseEnumDeserializerModifier extends ValueDeserializerModifier {

    @Override
    public ValueDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type,
                                                       BeanDescription.Supplier beanDesc,
                                                       ValueDeserializer<?> defaultDeserializer) {
        Class<?> rawClass = type.getRawClass();
        if (rawClass.isEnum() && IBaseEnum.class.isAssignableFrom(rawClass)) {
            return new IBaseEnumDeserializer(rawClass);
        }
        return defaultDeserializer;
    }
}
