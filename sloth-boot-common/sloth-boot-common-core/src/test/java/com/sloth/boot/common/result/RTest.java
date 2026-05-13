package com.sloth.boot.common.result;

import com.sloth.boot.common.exception.GlobalErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("统一响应结果 R<T> 测试")
class RTest {

    @Test
    @DisplayName("ok() 返回成功响应，code=0")
    void ok_returnsSuccessCode() {
        R<Void> r = R.ok();
        assertThat(r.getCode()).isZero();
        assertThat(r.getMsg()).isNotBlank();
        assertThat(r.isSuccess()).isTrue();
        assertThat(r.getTimestamp()).isPositive();
    }

    @Test
    @DisplayName("ok(data) 携带数据")
    void ok_withData() {
        R<String> r = R.ok("hello");
        assertThat(r.getCode()).isZero();
        assertThat(r.getData()).isEqualTo("hello");
    }

    @Test
    @DisplayName("ok(msg, data) 自定义消息和数据")
    void ok_withMsgAndData() {
        R<List<String>> r = R.ok("自定义消息", List.of("a", "b"));
        assertThat(r.getMsg()).isEqualTo("自定义消息");
        assertThat(r.getData()).containsExactly("a", "b");
    }

    @Test
    @DisplayName("fail() 返回失败响应，code=-1")
    void fail_returnsFailCode() {
        R<Void> r = R.fail();
        assertThat(r.getCode()).isEqualTo(-1);
        assertThat(r.getMsg()).isNotBlank();
        assertThat(r.isSuccess()).isFalse();
    }

    @Test
    @DisplayName("fail(msg) 自定义失败消息")
    void fail_withMessage() {
        R<Void> r = R.fail("自定义错误");
        assertThat(r.getCode()).isEqualTo(-1);
        assertThat(r.getMsg()).isEqualTo("自定义错误");
    }

    @Test
    @DisplayName("fail(code, msg) 自定义错误码和消息")
    void fail_withCodeAndMessage() {
        R<Void> r = R.fail(404, "未找到");
        assertThat(r.getCode()).isEqualTo(404);
        assertThat(r.getMsg()).isEqualTo("未找到");
    }

    @Test
    @DisplayName("fail(ErrorCode) 使用错误码枚举")
    void fail_withErrorCode() {
        R<Void> r = R.fail(GlobalErrorCode.UNAUTHORIZED);
        assertThat(r.getCode()).isEqualTo(401);
        assertThat(r.getMsg()).isEqualTo("未认证");
    }

    @Test
    @DisplayName("成功响应 traceId 为 null（未设置上下文时）")
    void traceId_nullWhenNoContext() {
        R<Void> r = R.ok();
        assertThat(r.getTraceId()).isNull();
    }
}
