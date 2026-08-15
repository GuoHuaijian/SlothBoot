package com.sloth.boot.common.security.desensitize;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 脱敏工具测试。
 */
@DisplayName("DesensitizeUtil 测试")
class DesensitizeUtilTest {

    @Test
    @DisplayName("身份证号末位含 X 时脱敏不泄露明文")
    void idCardWithTrailingX() {
        assertThat(DesensitizeUtil.idCard("11010519491231002X")).isEqualTo("1101****002X");
    }

    @Test
    @DisplayName("纯数字身份证号脱敏保留前后四位")
    void idCardNumeric() {
        assertThat(DesensitizeUtil.idCard("110105194912310021")).isEqualTo("1101****0021");
    }

    @Test
    @DisplayName("过短身份证号原样返回")
    void idCardTooShort() {
        assertThat(DesensitizeUtil.idCard("1234567")).isEqualTo("1234567");
    }

    @Test
    @DisplayName("空值原样返回")
    void idCardBlank() {
        assertThat(DesensitizeUtil.idCard("")).isEmpty();
        assertThat(DesensitizeUtil.idCard(null)).isNull();
    }
}
