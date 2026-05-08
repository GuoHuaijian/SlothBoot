package com.sloth.boot.common.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Long 转 String 序列化器
 * <p>
 * 解决前端 JavaScript 精度丢失问题（Long 超过 2^53 时精度丢失）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class ToStringSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }
}
