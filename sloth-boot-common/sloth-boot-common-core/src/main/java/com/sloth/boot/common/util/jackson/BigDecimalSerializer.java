package com.sloth.boot.common.util.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.math.BigDecimal;

/**
 * BigDecimal 序列化器
 * <p>
 * 将 BigDecimal 序列化为字符串，避免科学计数法和精度丢失。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class BigDecimalSerializer extends ValueSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializationContext provider) {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.stripTrailingZeros().toPlainString());
        }
    }
}
