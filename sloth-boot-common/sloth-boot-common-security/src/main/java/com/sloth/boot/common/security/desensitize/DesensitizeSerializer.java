package com.sloth.boot.common.security.desensitize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sloth.boot.common.annotation.Desensitize;
import com.sloth.boot.common.annotation.DesensitizeType;
import com.sloth.boot.common.util.DesensitizeUtil;

import java.io.IOException;

/**
 * 脱敏 JSON 序列化器。
 * <p>
 * 配合 {@link com.sloth.boot.common.annotation.Desensitize} 注解使用， 在 Jackson
 * 序列化时根据注解指定的脱敏类型自动对字段值进行脱敏处理。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class DesensitizeSerializer extends JsonSerializer<String> {

    /**
     * 序列化字段值，根据 {@link Desensitize} 注解配置进行脱敏。
     *
     * @param value    原始字段值
     * @param gen      JSON 生成器
     * @param provider 序列化提供者
     * @throws IOException IO 异常
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Desensitize desensitize = gen.getCurrentValue().getClass().getAnnotation(Desensitize.class);
        if (desensitize != null) {
            DesensitizeType type = desensitize.type();
            int prefixLen = desensitize.prefixLen();
            int suffixLen = desensitize.suffixLen();

            String desensitizedValue;
            switch (type) {
                case MOBILE:
                    desensitizedValue = DesensitizeUtil.mobilePhone(value);
                    break;
                case ID_CARD:
                    desensitizedValue = DesensitizeUtil.idCard(value);
                    break;
                case EMAIL:
                    desensitizedValue = DesensitizeUtil.email(value);
                    break;
                case BANK_CARD:
                    desensitizedValue = DesensitizeUtil.bankCard(value);
                    break;
                case NAME:
                    desensitizedValue = DesensitizeUtil.chineseName(value);
                    break;
                case ADDRESS:
                    desensitizedValue = DesensitizeUtil.address(value, 4);
                    break;
                case CUSTOM:
                    desensitizedValue = DesensitizeUtil.custom(value, prefixLen, suffixLen);
                    break;
                default:
                    desensitizedValue = value;
            }
            gen.writeString(desensitizedValue);
        } else {
            gen.writeString(value);
        }
    }
}
