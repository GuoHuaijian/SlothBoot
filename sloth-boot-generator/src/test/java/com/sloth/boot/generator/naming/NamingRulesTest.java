package com.sloth.boot.generator.naming;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NamingRulesTest {

    @Test
    void toClassNameShouldStripPrefixAndCamelize() {
        assertEquals("UserRole", NamingRules.toClassName("sys_user_role", new String[]{"sys_", "biz_"}));
        assertEquals("Order", NamingRules.toClassName("biz_order", new String[]{"sys_", "biz_"}));
        assertEquals("SysUser", NamingRules.toClassName("sys_user", new String[]{}));
        assertEquals("User", NamingRules.toClassName("user", null));
    }

    @Test
    void toFieldNameShouldCamelizeColumn() {
        assertEquals("userName", NamingRules.toFieldName("user_name"));
        assertEquals("id", NamingRules.toFieldName("id"));
        assertEquals("createTime", NamingRules.toFieldName("create_time"));
    }

    @Test
    void toResourceSegmentShouldPluralize() {
        assertEquals("users", NamingRules.toResourceSegment("User"));
        assertEquals("categories", NamingRules.toResourceSegment("Category"));
        assertEquals("boxes", NamingRules.toResourceSegment("Box"));
        assertEquals("buses", NamingRules.toResourceSegment("Bus"));
        assertEquals("matches", NamingRules.toResourceSegment("Match"));
    }

    @Test
    void toVariableNameShouldUncapitalize() {
        assertEquals("user", NamingRules.toVariableName("User"));
        assertEquals("sysUser", NamingRules.toVariableName("SysUser"));
    }
}
