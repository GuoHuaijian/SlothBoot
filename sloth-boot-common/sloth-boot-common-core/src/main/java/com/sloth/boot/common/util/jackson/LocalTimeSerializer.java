package com.sloth.boot.common.util.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sloth.boot.common.constant.CommonConstant;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalTime 序列化器
 * <p>
 * 优先使用字段上的 {@link JsonFormat} 指定格式，未指定时使用全局默认格式。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class LocalTimeSerializer extends ValueSerializer<LocalTime> {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern(CommonConstant.DEFAULT_TIME_FORMAT);

    private final DateTimeFormatter formatter;

    public LocalTimeSerializer() {
        this(DEFAULT_FORMATTER);
    }

    public LocalTimeSerializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
        if (property != null) {
            JsonFormat.Value format = property.findFormatOverrides(ctxt.getConfig());
            if (format != null && format.hasPattern()) {
                return new LocalTimeSerializer(DateTimeFormatter.ofPattern(format.getPattern()));
            }
        }
        return this;
    }

    @Override
    public void serialize(LocalTime value, JsonGenerator gen, SerializationContext provider) {
        gen.writeString(value.format(formatter));
    }
}
