package com.sloth.boot.common.util.jackson;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.sloth.boot.common.enums.IBaseEnum;

/**
 * IBaseEnum 反序列化器
 * <p>
 * 根据 code 值反序列化为对应的枚举实例。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class IBaseEnumDeserializer extends ValueDeserializer<IBaseEnum> {

    @Override
    public IBaseEnum deserialize(JsonParser p, DeserializationContext ctxt) {
        int code = p.getIntValue();
        Class<?> enumClass = ctxt.getContextualType().getRawClass();
        if (!enumClass.isEnum() || !IBaseEnum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("类型不是 IBaseEnum 枚举: " + enumClass.getName());
        }

        Object[] enumConstants = enumClass.getEnumConstants();
        for (Object enumConstant : enumConstants) {
            IBaseEnum baseEnum = (IBaseEnum) enumConstant;
            if (baseEnum.getCode() == code) {
                return baseEnum;
            }
        }
        throw new IllegalArgumentException("枚举 " + enumClass.getName() + " 中没有值为 " + code + " 的枚举");
    }
}
