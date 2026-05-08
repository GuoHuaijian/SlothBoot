package com.sloth.boot.common.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * BigDecimal 序列化器
 * <p>
 * 将 BigDecimal 序列化为字符串，避免科学计数法和精度丢失。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.stripTrailingZeros().toPlainString());
        }
    }
}
