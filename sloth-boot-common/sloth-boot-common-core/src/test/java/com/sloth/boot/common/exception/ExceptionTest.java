package com.sloth.boot.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("异常体系测试")
class ExceptionTest {

    @Nested
    @DisplayName("GlobalErrorCode")
    class GlobalErrorCodeTest {

        @Test
        @DisplayName("所有枚举值 msg 非空")
        void allEnumValuesHaveMsg() {
            for (GlobalErrorCode errorCode : GlobalErrorCode.values()) {
                assertThat(errorCode.getMsg()).isNotBlank();
            }
        }

        @Test
        @DisplayName("SUCCESS 的 code 为 0")
        void successCodeIsZero() {
            assertThat(GlobalErrorCode.SUCCESS.getCode()).isZero();
        }
    }

    @Nested
    @DisplayName("BaseException")
    class BaseExceptionTest {

        @Test
        @DisplayName("构造时保留 ErrorCode")
        void preservesErrorCode() {
            BaseException ex = new BaseException(GlobalErrorCode.BAD_REQUEST);
            assertThat(ex.getErrorCode()).isEqualTo(GlobalErrorCode.BAD_REQUEST);
            assertThat(ex.getCode()).isEqualTo(400);
            assertThat(ex.getMessage()).isEqualTo("请求参数错误");
        }

        @Test
        @DisplayName("自定义消息覆盖 ErrorCode 默认消息")
        void customMessageOverridesDefault() {
            BaseException ex = new BaseException(GlobalErrorCode.BAD_REQUEST, "字段不能为空");
            assertThat(ex.getMessage()).isEqualTo("字段不能为空");
            assertThat(ex.getCode()).isEqualTo(400);
        }

        @Test
        @DisplayName("支持 cause 链")
        void supportsCauseChain() {
            RuntimeException cause = new RuntimeException("root");
            BaseException ex = new BaseException(GlobalErrorCode.INTERNAL_ERROR, cause);
            assertThat(ex.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("BizException")
    class BizExceptionTest {

        @Test
        @DisplayName("of(ErrorCode) 工厂方法")
        void ofErrorCode() {
            BizException ex = BizException.of(GlobalErrorCode.NOT_FOUND);
            assertThat(ex.getCode()).isEqualTo(404);
        }

        @Test
        @DisplayName("of(ErrorCode, message) 工厂方法")
        void ofErrorCodeAndMessage() {
            BizException ex = BizException.of(GlobalErrorCode.FORBIDDEN, "无权访问");
            assertThat(ex.getCode()).isEqualTo(403);
            assertThat(ex.getMessage()).isEqualTo("无权访问");
        }

        @Test
        @DisplayName("of(code, message) 工厂方法使用 SimpleErrorCode")
        void ofCodeAndMessage() {
            BizException ex = BizException.of(1001, "业务错误");
            assertThat(ex.getCode()).isEqualTo(1001);
            assertThat(ex.getMessage()).isEqualTo("业务错误");
        }

        @Test
        @DisplayName("of(message) 工厂方法使用 INTERNAL_ERROR")
        void ofMessage() {
            BizException ex = BizException.of("出错了");
            assertThat(ex.getCode()).isEqualTo(500);
            assertThat(ex.getMessage()).isEqualTo("出错了");
        }

        @Test
        @DisplayName("BizException 是 RuntimeException 的子类")
        void isRuntimeException() {
            assertThatThrownBy(() -> {
                throw BizException.of(GlobalErrorCode.BAD_REQUEST);
            }).isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("SystemException")
    class SystemExceptionTest {

        @Test
        @DisplayName("of(code, message) 工厂方法")
        void ofCodeAndMessage() {
            SystemException ex = SystemException.of(500, "系统错误");
            assertThat(ex.getCode()).isEqualTo(500);
            assertThat(ex.getMessage()).isEqualTo("系统错误");
        }
    }
}
