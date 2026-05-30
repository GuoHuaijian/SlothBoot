package com.sloth.boot.common.util.jackson;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.sloth.boot.common.constant.CommonConstant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate 反序列化器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class LocalDateDeserializer extends ValueDeserializer<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(CommonConstant.DEFAULT_DATE_FORMAT);

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) {
        String text = p.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }
        return LocalDate.parse(text, FORMATTER);
    }
}
