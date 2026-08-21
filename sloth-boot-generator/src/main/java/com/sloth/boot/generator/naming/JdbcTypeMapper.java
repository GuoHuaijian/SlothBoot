package com.sloth.boot.generator.naming;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * JDBC 类型到 Java 类型的映射。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public enum JdbcTypeMapper {

    BOOLEAN(Types.BIT, "Boolean"),
    BOOLEAN2(Types.BOOLEAN, "Boolean"),

    INTEGER(Types.TINYINT, "Integer"),
    INTEGER2(Types.SMALLINT, "Integer"),
    INTEGER3(Types.INTEGER, "Integer"),

    LONG(Types.BIGINT, "Long"),

    FLOAT(Types.REAL, "Float"),
    DOUBLE(Types.FLOAT, "Double"),
    DOUBLE2(Types.DOUBLE, "Double"),

    DECIMAL(Types.DECIMAL, "BigDecimal"),
    DECIMAL2(Types.NUMERIC, "BigDecimal"),

    DATE(Types.DATE, "LocalDate"),
    TIME(Types.TIME, "LocalTime"),
    DATETIME(Types.TIMESTAMP, "LocalDateTime"),
    DATETIME_WITH_TZ(Types.TIMESTAMP_WITH_TIMEZONE, "OffsetDateTime"),

    BYTES(Types.BINARY, "byte[]"),
    BYTES2(Types.VARBINARY, "byte[]"),
    BYTES3(Types.LONGVARBINARY, "byte[]"),
    BLOB(Types.BLOB, "byte[]"),

    STRING(Types.CHAR, "String"),
    STRING2(Types.VARCHAR, "String"),
    STRING3(Types.LONGVARCHAR, "String"),
    STRING4(Types.NCHAR, "String"),
    STRING5(Types.NVARCHAR, "String"),
    STRING6(Types.LONGNVARCHAR, "String"),
    CLOB(Types.CLOB, "String");

    /**
     * java.sql.Types 类型码
     */
    private final int typeCode;

    /**
     * Java 类型简单名（如 Long、BigDecimal）
     */
    private final String javaType;

    JdbcTypeMapper(int typeCode, String javaType) {
        this.typeCode = typeCode;
        this.javaType = javaType;
    }

    /**
     * 类型码 → Java 简单类型名映射表（供快速查找）。
     */
    private static final Map<Integer, String> TYPE_CODE_MAP;

    static {
        Map<Integer, String> map = new HashMap<>();
        for (JdbcTypeMapper mapper : values()) {
            map.putIfAbsent(mapper.typeCode, mapper.javaType);
        }
        TYPE_CODE_MAP = Map.copyOf(map);
    }

    /**
     * 将 JDBC 类型码映射为 Java 类型简单名，未知类型回退为 Object 并由调用方告警。
     *
     * @param typeCode java.sql.Types 类型码
     * @return Java 类型简单名
     */
    public static String toJavaType(int typeCode) {
        return TYPE_CODE_MAP.getOrDefault(typeCode, "Object");
    }

    /**
     * 判断 Java 类型是否为字符串类型（用于决定 like/eq 查询方式与校验注解）。
     *
     * @param javaType Java 类型简单名
     * @return 是否字符串
     */
    public static boolean isStringType(String javaType) {
        return "String".equals(javaType);
    }

    /**
     * 判断 Java 类型是否需要显式 import（排除 java.lang 包与基本类型包装）。
     *
     * @param javaType Java 类型简单名
     * @return 是否需要 import
     */
    public static boolean requiresImport(String javaType) {
        return switch (javaType) {
            case "String", "Byte", "Short", "Integer", "Long", "Float", "Double",
                 "Boolean", "Character", "Object", "byte[]" -> false;
            default -> true;
        };
    }
}
