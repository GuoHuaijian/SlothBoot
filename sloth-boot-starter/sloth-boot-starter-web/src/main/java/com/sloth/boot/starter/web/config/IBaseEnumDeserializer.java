package com.sloth.boot.starter.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.sloth.boot.common.enums.IBaseEnum;

import java.io.IOException;

/**
 * IBaseEnum 反序列化器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class IBaseEnumDeserializer extends JsonDeserializer<IBaseEnum> {

    @Override
    public IBaseEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int code = p.getIntValue();
        Class<?> enumClass = ctxt.getContextualType().getRawClass();
        if (!enumClass.isEnum() || !IBaseEnum.class.isAssignableFrom(enumClass)) {
            throw new IOException("类型不是 IBaseEnum 枚举: " + enumClass.getName());
        }

        Object[] enumConstants = enumClass.getEnumConstants();
        for (Object enumConstant : enumConstants) {
            IBaseEnum baseEnum = (IBaseEnum) enumConstant;
            if (baseEnum.getCode() == code) {
                return baseEnum;
            }
        }
        throw new IOException("枚举 " + enumClass.getName() + " 中没有值为 " + code + " 的枚举");
    }
}
