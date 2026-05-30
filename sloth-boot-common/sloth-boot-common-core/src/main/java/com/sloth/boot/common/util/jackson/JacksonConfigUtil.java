package com.sloth.boot.common.util.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sloth.boot.common.enums.IBaseEnum;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Jackson 统一配置工具类。
 * <p>
 * Jackson 3.x 中 {@link ObjectMapper} 不可变，配置通过 {@link JsonMapper.Builder} 完成。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class JacksonConfigUtil {

    private JacksonConfigUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 创建已配置的 ObjectMapper（含自定义序列化器）
     *
     * @return ObjectMapper
     */
    public static ObjectMapper createConfiguredMapper() {
        return createConfiguredMapper(Collections.emptyList());
    }

    /**
     * 创建已配置的 ObjectMapper，附加额外模块。
     *
     * @param extraModules 额外的 Jackson 模块（如脱敏模块）
     * @return ObjectMapper
     */
    public static ObjectMapper createConfiguredMapper(List<JacksonModule> extraModules) {
        JsonMapper.Builder builder = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        builder.addModule(createCustomSerializersModule());
        for (JacksonModule module : extraModules) {
            builder.addModule(module);
        }
        return builder.build();
    }

    /**
     * 创建包含自定义序列化器/反序列化器的模块。
     *
     * @return 自定义序列化器模块
     */
    public static SimpleModule createCustomSerializersModule() {
        SimpleModule module = new SimpleModule("sloth-custom-serializers");
        module.addSerializer(Long.class, new ToStringSerializer());
        module.addSerializer(long.class, new ToStringSerializer());
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        module.addSerializer(LocalDate.class, new LocalDateSerializer());
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        module.addSerializer(LocalTime.class, new LocalTimeSerializer());
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        module.addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
        module.addSerializer(IBaseEnum.class, new IBaseEnumSerializer());
        module.addDeserializer(IBaseEnum.class, new IBaseEnumDeserializer());
        return module;
    }
}
