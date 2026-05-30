package com.sloth.boot.common.log.annotation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 操作日志注解测试。
 */
class OperateLogTest {

    @Test
    void should_have_default_values() throws NoSuchMethodException {
        OperateLog annotation = SampleClass.class.getMethod("annotatedMethod").getAnnotation(OperateLog.class);

        assertThat(annotation.module()).isEmpty();
        assertThat(annotation.description()).isEmpty();
        assertThat(annotation.type()).isEqualTo(OperateTypeEnum.OTHER);
        assertThat(annotation.saveRequestData()).isTrue();
        assertThat(annotation.saveResponseData()).isTrue();
    }

    @Test
    void should_support_all_operate_types() {
        assertThat(OperateTypeEnum.values()).hasSize(9);
        assertThat(OperateTypeEnum.QUERY.getCode()).isEqualTo(1);
        assertThat(OperateTypeEnum.DELETE.getCode()).isEqualTo(4);
        assertThat(OperateTypeEnum.LOGIN.getCode()).isEqualTo(7);
    }

    static class SampleClass {
        @OperateLog
        public void annotatedMethod() {
        }
    }
}
