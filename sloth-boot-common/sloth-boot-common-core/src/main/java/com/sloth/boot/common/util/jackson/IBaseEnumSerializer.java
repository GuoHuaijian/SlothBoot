package com.sloth.boot.common.util.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import com.sloth.boot.common.enums.IBaseEnum;


/**
 * IBaseEnum 序列化器
 * <p>
 * 将 IBaseEnum 序列化为 {@code {"code": 1, "desc": "xxx"}} 格式。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class IBaseEnumSerializer extends ValueSerializer<IBaseEnum> {

    @Override
    public void serialize(IBaseEnum value, JsonGenerator gen, SerializationContext provider) {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeStartObject();
            gen.writeName("code");
            gen.writeNumber(value.getCode());
            gen.writeName("desc");
            gen.writeString(value.getDesc());
            gen.writeEndObject();
        }
    }
}
