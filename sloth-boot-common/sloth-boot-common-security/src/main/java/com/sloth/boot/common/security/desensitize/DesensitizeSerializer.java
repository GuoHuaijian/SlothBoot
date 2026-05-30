package com.sloth.boot.common.security.desensitize;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import com.sloth.boot.common.util.DesensitizeUtil;

/**
 * 脱敏 JSON 序列化器。
 * <p>
 * 由 {@link DesensitizeValueSerializerModifier} 注入，通过构造函数传入
 * 字段上的 {@link Desensitize} 注解实例。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class DesensitizeSerializer extends ValueSerializer<String> {

    private final Desensitize desensitize;

    public DesensitizeSerializer(Desensitize desensitize) {
        super();
        this.desensitize = desensitize;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext provider) {
        if (value == null) {
            gen.writeNull();
            return;
        }
        String result = switch (desensitize.type()) {
            case MOBILE -> DesensitizeUtil.mobilePhone(value);
            case ID_CARD -> DesensitizeUtil.idCard(value);
            case EMAIL -> DesensitizeUtil.email(value);
            case BANK_CARD -> DesensitizeUtil.bankCard(value);
            case NAME -> DesensitizeUtil.chineseName(value);
            case ADDRESS -> DesensitizeUtil.address(value, 4);
            case CUSTOM -> DesensitizeUtil.custom(value, desensitize.prefixLen(), desensitize.suffixLen());
        };
        gen.writeString(result);
    }
}
