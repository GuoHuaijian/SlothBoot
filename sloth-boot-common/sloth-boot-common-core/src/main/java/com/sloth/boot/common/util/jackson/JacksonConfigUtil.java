package com.sloth.boot.common.util.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sloth.boot.common.enums.IBaseEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Jackson 统一配置工具类
 * <p>
 * 提供统一的 {@link ObjectMapper} 配置方法，注册所有自定义序列化器/反序列化器。
 * 确保 Web 层和 Redis 层使用相同的序列化策略。
 * <p>
 * 使用示例：
 * <pre>
 * // 获取已配置的 ObjectMapper
 * ObjectMapper mapper = JacksonConfigUtil.createConfiguredMapper();
 *
 * // 或者向已有的 ObjectMapper 注册自定义序列化器
 * JacksonConfigUtil.registerCustomSerializers(existingMapper);
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class JacksonConfigUtil {

    private JacksonConfigUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 创建已配置的 ObjectMapper（注册所有自定义序列化器）
     *
     * @return ObjectMapper
     */
    public static ObjectMapper createConfiguredMapper() {
        ObjectMapper mapper = new ObjectMapper();
        configureMapper(mapper);
        return mapper;
    }

    /**
     * 配置 ObjectMapper（基础设置 + 自定义序列化器）
     *
     * @param mapper ObjectMapper
     */
    public static void configureMapper(ObjectMapper mapper) {
        // 基础配置
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 注册 JavaTimeModule
        mapper.registerModule(new JavaTimeModule());

        // 注册自定义序列化器
        registerCustomSerializers(mapper);
    }

    /**
     * 向已有的 ObjectMapper 注册自定义序列化器/反序列化器
     *
     * @param mapper ObjectMapper
     */
    public static void registerCustomSerializers(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
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
        mapper.registerModule(module);
    }
}
