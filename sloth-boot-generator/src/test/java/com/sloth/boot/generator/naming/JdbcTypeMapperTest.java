package com.sloth.boot.generator.naming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Types;

import org.junit.jupiter.api.Test;

class JdbcTypeMapperTest {

    @Test
    void toJavaTypeShouldMapCommonTypes() {
        assertEquals("Long", JdbcTypeMapper.toJavaType(Types.BIGINT));
        assertEquals("Integer", JdbcTypeMapper.toJavaType(Types.INTEGER));
        assertEquals("Integer", JdbcTypeMapper.toJavaType(Types.TINYINT));
        assertEquals("String", JdbcTypeMapper.toJavaType(Types.VARCHAR));
        assertEquals("String", JdbcTypeMapper.toJavaType(Types.LONGNVARCHAR));
        assertEquals("BigDecimal", JdbcTypeMapper.toJavaType(Types.DECIMAL));
        assertEquals("LocalDate", JdbcTypeMapper.toJavaType(Types.DATE));
        assertEquals("LocalDateTime", JdbcTypeMapper.toJavaType(Types.TIMESTAMP));
        assertEquals("Boolean", JdbcTypeMapper.toJavaType(Types.BIT));
        assertEquals("byte[]", JdbcTypeMapper.toJavaType(Types.BLOB));
    }

    @Test
    void unknownTypeShouldFallbackToObject() {
        assertEquals("Object", JdbcTypeMapper.toJavaType(Types.REF_CURSOR));
        assertEquals("Object", JdbcTypeMapper.toJavaType(Integer.MIN_VALUE));
    }

    @Test
    void stringTypeShouldDriveLikeSearch() {
        assertTrue(JdbcTypeMapper.isStringType("String"));
        assertFalse(JdbcTypeMapper.isStringType("Integer"));
    }

    @Test
    void requiresImportShouldExcludeLangTypes() {
        assertFalse(JdbcTypeMapper.requiresImport("String"));
        assertFalse(JdbcTypeMapper.requiresImport("Long"));
        assertFalse(JdbcTypeMapper.requiresImport("byte[]"));
        assertTrue(JdbcTypeMapper.requiresImport("BigDecimal"));
        assertTrue(JdbcTypeMapper.requiresImport("LocalDateTime"));
    }
}
