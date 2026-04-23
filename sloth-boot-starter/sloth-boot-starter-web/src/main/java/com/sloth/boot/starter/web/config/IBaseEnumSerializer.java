package com.sloth.boot.starter.web.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sloth.boot.common.enums.IBaseEnum;

import java.io.IOException;

/**
 * IBaseEnum 序列化器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class IBaseEnumSerializer extends JsonSerializer<IBaseEnum> {

    @Override
    public void serialize(IBaseEnum value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeStartObject();
            gen.writeNumberField("code", value.getCode());
            gen.writeStringField("desc", value.getDesc());
            gen.writeEndObject();
        }
    }
}