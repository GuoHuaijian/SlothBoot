package com.sloth.boot.common.util.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import com.sloth.boot.common.constant.CommonConstant;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalTime 序列化器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class LocalTimeSerializer extends ValueSerializer<LocalTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(CommonConstant.DEFAULT_TIME_FORMAT);

    @Override
    public void serialize(LocalTime value, JsonGenerator gen, SerializationContext provider) {
        gen.writeString(value.format(FORMATTER));
    }
}
