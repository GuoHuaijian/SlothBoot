package com.sloth.boot.common.util.jackson;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.math.BigDecimal;

/**
 * BigDecimal 反序列化器
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class BigDecimalDeserializer extends ValueDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) {
        String text = p.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }
        return new BigDecimal(text);
    }
}
