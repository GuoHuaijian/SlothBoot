package com.sloth.boot.common.util.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import com.sloth.boot.common.constant.CommonConstant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate 序列化器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class LocalDateSerializer extends ValueSerializer<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(CommonConstant.DEFAULT_DATE_FORMAT);

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializationContext provider) {
        gen.writeString(value.format(FORMATTER));
    }
}
