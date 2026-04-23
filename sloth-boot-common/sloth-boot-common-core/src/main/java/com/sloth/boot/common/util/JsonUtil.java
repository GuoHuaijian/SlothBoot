package com.sloth.boot.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * JSON 工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        // 配置日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        OBJECT_MAPPER.setDateFormat(dateFormat);
        // 配置时区
        OBJECT_MAPPER.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8"));
        // 配置序列化特性
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        // 配置反序列化特性
        OBJECT_MAPPER.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 配置空值处理
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 注册 JavaTimeModule
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * 对象转 JSON 字符串
     *
     * @param obj 对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转 JSON 失败", e);
            throw new RuntimeException("对象转 JSON 失败", e);
        }
    }

    /**
     * JSON 字符串转对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类
     * @param <T>   目标类型
     * @return 对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("JSON 转对象失败", e);
            throw new RuntimeException("JSON 转对象失败", e);
        }
    }

    /**
     * JSON 字符串转复杂类型对象
     *
     * @param json          JSON 字符串
     * @param typeReference 类型引用
     * @param <T>           目标类型
     * @return 对象
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("JSON 转复杂类型对象失败", e);
            throw new RuntimeException("JSON 转复杂类型对象失败", e);
        }
    }

    /**
     * JSON 字符串转列表
     *
     * @param json  JSON 字符串
     * @param clazz 列表元素类
     * @param <T>   列表元素类型
     * @return 列表
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("JSON 转列表失败", e);
            throw new RuntimeException("JSON 转列表失败", e);
        }
    }

    /**
     * 获取 Object Mapper 实例
     *
     * @return Object Mapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
