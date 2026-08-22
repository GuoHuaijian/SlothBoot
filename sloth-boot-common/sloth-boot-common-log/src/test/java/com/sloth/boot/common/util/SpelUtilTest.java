package com.sloth.boot.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SpEL 表达式解析测试。
 */
@DisplayName("SpelUtil 测试")
class SpelUtilTest {

    private Method orderMethod() throws NoSuchMethodException {
        return this.getClass().getDeclaredMethod("order", String.class, String.class);
    }

    @SuppressWarnings("unused")
    private void order(String userId, String orderId) {
        // 仅供 SpEL 参数名解析使用
    }

    @Test
    @DisplayName("普通 SpEL 解析方法参数")
    void parsePlainExpression() throws NoSuchMethodException {
        String result = SpelUtil.parse(this, orderMethod(), new Object[]{"u1", "o1"}, "#userId", "default");

        assertThat(result).isEqualTo("u1");
    }

    @Test
    @DisplayName("模板表达式 #{...} 正确解析拼接")
    void parseTemplateExpression() throws NoSuchMethodException {
        String result = SpelUtil.parse(this, orderMethod(), new Object[]{"u1", "o1"},
            "#{#userId + '_' + #orderId}", "default");

        assertThat(result).isEqualTo("u1_o1");
    }

    @Test
    @DisplayName("无 # 与 #{} 的字面量原样返回")
    void parseLiteralExpression() throws NoSuchMethodException {
        String result = SpelUtil.parse(this, orderMethod(), new Object[]{"u1", "o1"}, "static-key", "default");

        assertThat(result).isEqualTo("static-key");
    }

    @Test
    @DisplayName("空表达式返回默认值")
    void parseBlankExpressionReturnsDefault() throws NoSuchMethodException {
        assertThat(SpelUtil.parse(this, orderMethod(), new Object[]{}, " ", "default")).isEqualTo("default");
        assertThat(SpelUtil.parse(this, orderMethod(), new Object[]{}, null, "default")).isEqualTo("default");
    }
}
