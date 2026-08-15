package com.sloth.boot.common.util.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sloth.boot.common.constant.CommonConstant;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate 序列化器
 * <p>
 * 优先使用字段上的 {@link JsonFormat} 指定格式，未指定时使用全局默认格式。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class LocalDateSerializer extends ValueSerializer<LocalDate> {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern(CommonConstant.DEFAULT_DATE_FORMAT);

    private final DateTimeFormatter formatter;

    public LocalDateSerializer() {
        this(DEFAULT_FORMATTER);
    }

    public LocalDateSerializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
        if (property != null) {
            JsonFormat.Value format = property.findFormatOverrides(ctxt.getConfig());
            if (format != null && format.hasPattern()) {
                return new LocalDateSerializer(DateTimeFormatter.ofPattern(format.getPattern()));
            }
        }
        return this;
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializationContext provider) {
        gen.writeString(value.format(formatter));
    }
}
