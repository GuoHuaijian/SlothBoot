package com.sloth.boot.common.util.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;


/**
 * Long 转 String 序列化器
 * <p>
 * 解决前端 JavaScript 精度丢失问题（Long 超过 2^53 时精度丢失）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class ToStringSerializer extends ValueSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializationContext provider) {
        gen.writeString(value.toString());
    }
}
