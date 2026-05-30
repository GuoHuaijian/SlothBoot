package com.sloth.boot.common.util.jackson;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.sloth.boot.common.constant.CommonConstant;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalTime 反序列化器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class LocalTimeDeserializer extends ValueDeserializer<LocalTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(CommonConstant.DEFAULT_TIME_FORMAT);

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) {
        String text = p.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }
        return LocalTime.parse(text, FORMATTER);
    }
}
