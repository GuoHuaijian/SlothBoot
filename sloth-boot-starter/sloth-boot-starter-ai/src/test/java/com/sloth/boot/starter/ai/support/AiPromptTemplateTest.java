package com.sloth.boot.starter.ai.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AiPromptTemplate 单元测试")
class AiPromptTemplateTest {

    @Test
    @DisplayName("渲染单个变量")
    void render_singleVariable() {
        String result = AiPromptTemplate.render("你好，{{name}}！", Map.of("name", "张三"));
        assertThat(result).isEqualTo("你好，张三！");
    }

    @Test
    @DisplayName("渲染多个变量")
    void render_multipleVariables() {
        String template = "{{role}}专家，请用{{language}}回答：{{question}}";
        String result = AiPromptTemplate.render(template, Map.of(
            "role", "Java",
            "language", "中文",
            "question", "什么是 IoC？"
        ));
        assertThat(result).isEqualTo("Java专家，请用中文回答：什么是 IoC？");
    }

    @Test
    @DisplayName("null 模板返回 null")
    void render_nullTemplate_returnsNull() {
        String result = AiPromptTemplate.render(null, Map.of("key", "value"));
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("空模板返回空字符串")
    void render_emptyTemplate_returnsEmpty() {
        String result = AiPromptTemplate.render("", Map.of("key", "value"));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("null 变量返回原模板")
    void render_nullVariables_returnsTemplate() {
        String result = AiPromptTemplate.render("你好{{name}}", null);
        assertThat(result).isEqualTo("你好{{name}}");
    }

    @Test
    @DisplayName("空变量返回原模板")
    void render_emptyVariables_returnsTemplate() {
        String result = AiPromptTemplate.render("你好{{name}}", Map.of());
        assertThat(result).isEqualTo("你好{{name}}");
    }

    @Test
    @DisplayName("缺失的变量键保留占位符")
    void render_missingKey_keepsPlaceholder() {
        String result = AiPromptTemplate.render("你好{{name}}，你是{{age}}岁", Map.of("name", "张三"));
        assertThat(result).isEqualTo("你好张三，你是{{age}}岁");
    }

    @Test
    @DisplayName("null 值替换为空字符串")
    void render_nullValue_replacesWithEmpty() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", null);
        String result = AiPromptTemplate.render("你好{{name}}", variables);
        assertThat(result).isEqualTo("你好");
    }
}
