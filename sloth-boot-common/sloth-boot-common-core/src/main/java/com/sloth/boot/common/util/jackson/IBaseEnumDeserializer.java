package com.sloth.boot.common.util.jackson;

import com.sloth.boot.common.enums.IBaseEnum;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;

/**
 * IBaseEnum 反序列化器
 * <p>
 * 支持从 {@code {"code": 1, "desc": "xxx"}} 对象格式或裸 code 数字格式反序列化，
 * 与 {@link IBaseEnumSerializer} 的序列化格式保持一致。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class IBaseEnumDeserializer extends ValueDeserializer<IBaseEnum> {

    private static final String CODE_FIELD = "code";

    /**
     * 由 {@link IBaseEnumDeserializerModifier} 传入的目标枚举类型，根值反序列化时
     * {@code getContextualType()} 可能为 null，此时使用该字段。
     */
    private final Class<?> enumClass;

    public IBaseEnumDeserializer() {
        this(null);
    }

    public IBaseEnumDeserializer(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public IBaseEnum deserialize(JsonParser p, DeserializationContext ctxt) {
        Class<?> targetClass = resolveEnumClass(ctxt);
        if (targetClass == null || !targetClass.isEnum() || !IBaseEnum.class.isAssignableFrom(targetClass)) {
            throw ctxt.weirdStringException(p.getText(), Object.class, "类型不是 IBaseEnum 枚举");
        }

        int code = readCode(p);
        for (Object enumConstant : targetClass.getEnumConstants()) {
            IBaseEnum baseEnum = (IBaseEnum) enumConstant;
            if (baseEnum.getCode() == code) {
                return baseEnum;
            }
        }
        throw ctxt.weirdStringException(String.valueOf(code), targetClass, "枚举中没有值为 " + code + " 的枚举");
    }

    private Class<?> resolveEnumClass(DeserializationContext ctxt) {
        if (enumClass != null) {
            return enumClass;
        }
        JavaType contextualType = ctxt.getContextualType();
        return contextualType != null ? contextualType.getRawClass() : null;
    }

    private int readCode(JsonParser p) {
        if (p.currentToken() == JsonToken.START_OBJECT) {
            return readCodeFromObject(p);
        }
        return p.getValueAsInt();
    }

    private int readCodeFromObject(JsonParser p) {
        int code = 0;
        while (p.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = p.currentName();
            p.nextToken();
            if (CODE_FIELD.equals(fieldName)) {
                code = p.getValueAsInt();
            } else {
                p.skipChildren();
            }
        }
        return code;
    }
}
