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
        try {
            return IBaseEnum.fromCode((Class<IBaseEnum>) enumClass, code);
        } catch (Exception e) {
            throw new IOException("枚举反序列化失败", e);
        }
    }
}