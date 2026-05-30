package com.sloth.boot.common.util;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.sloth.boot.common.exception.SystemException;
import com.sloth.boot.common.util.jackson.JacksonConfigUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JSON 工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public final class JsonUtil {

    private JsonUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final ObjectMapper OBJECT_MAPPER = JacksonConfigUtil.createConfiguredMapper();

    /**
     * 对象转 JSON 字符串
     *
     * @param obj 对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JacksonException e) {
            log.error("[Json] Object to JSON failed", e);
            throw SystemException.of("Object to JSON failed", e);
        }
    }

    /**
     * 对象转格式化的 JSON 字符串
     *
     * @param obj 对象
     * @return 格式化的 JSON 字符串
     */
    public static String toJsonPretty(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JacksonException e) {
            log.error("[Json] Object to pretty JSON failed", e);
            throw SystemException.of("Object to pretty JSON failed", e);
        }
    }

    /**
     * 对象转 JSON 字节数组
     *
     * @param obj 对象
     * @return JSON 字节数组
     */
    public static byte[] toBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (JacksonException e) {
            log.error("[Json] Object to JSON bytes failed", e);
            throw SystemException.of("Object to JSON bytes failed", e);
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
        } catch (JacksonException e) {
            log.error("[Json] JSON to object failed", e);
            throw SystemException.of("JSON to object failed", e);
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
        } catch (JacksonException e) {
            log.error("[Json] JSON to parameterized type failed", e);
            throw SystemException.of("JSON to parameterized type failed", e);
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
        } catch (JacksonException e) {
            log.error("[Json] JSON to list failed", e);
            throw SystemException.of("JSON to list failed", e);
        }
    }

    /**
     * 解析 JSON 字符串为 JsonNode 树
     *
     * @param json JSON 字符串
     * @return JsonNode
     */
    public static JsonNode readTree(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JacksonException e) {
            log.error("[Json] JSON parse to JsonNode failed", e);
            throw SystemException.of("JSON parse to JsonNode failed", e);
        }
    }

    /**
     * 校验字符串是否为有效 JSON
     *
     * @param json 字符串
     * @return 是否为有效 JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        try {
            OBJECT_MAPPER.readTree(json);
            return true;
        } catch (JacksonException e) {
            return false;
        }
    }

    /**
     * 对象类型转换（通过 JSON 序列化/反序列化）
     *
     * @param fromValue   源对象
     * @param toValueType 目标类型
     * @param <T>         目标类型
     * @return 转换后的对象
     */
    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return OBJECT_MAPPER.convertValue(fromValue, toValueType);
    }

    /**
     * 合并两个 JSON 对象（后者覆盖前者）
     *
     * @param baseJson    基础 JSON 字符串
     * @param overlayJson 覆盖 JSON 字符串
     * @return 合并后的 JSON 字符串
     */
    public static String mergeJson(String baseJson, String overlayJson) {
        try {
            JsonNode baseNode = OBJECT_MAPPER.readTree(baseJson);
            JsonNode overlayNode = OBJECT_MAPPER.readTree(overlayJson);
            JsonNode merged = baseNode.deepCopy();
            merged = OBJECT_MAPPER.readerForUpdating(merged).readValue(overlayNode);
            return OBJECT_MAPPER.writeValueAsString(merged);
        } catch (JacksonException e) {
            log.error("[Json] JSON merge failed", e);
            throw SystemException.of("JSON merge failed", e);
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
