package com.sloth.boot.common.util.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sloth.boot.common.constant.CommonConstant;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalTime 反序列化器
 * <p>
 * 优先使用字段上的 {@link JsonFormat} 指定格式，未指定时使用全局默认格式。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class LocalTimeDeserializer extends ValueDeserializer<LocalTime> {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern(CommonConstant.DEFAULT_TIME_FORMAT);

    private final DateTimeFormatter formatter;

    public LocalTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public LocalTimeDeserializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        if (property != null) {
            JsonFormat.Value format = property.findFormatOverrides(ctxt.getConfig());
            if (format != null && format.hasPattern()) {
                return new LocalTimeDeserializer(DateTimeFormatter.ofPattern(format.getPattern()));
            }
        }
        return this;
    }

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) {
        String text = p.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }
        return LocalTime.parse(text, formatter);
    }
}
