package com.sloth.boot.starter.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.sloth.boot.common.constant.CommonConstant;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate 反序列化器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(CommonConstant.DEFAULT_DATE_FORMAT);

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }
        return LocalDate.parse(text, FORMATTER);
    }
}