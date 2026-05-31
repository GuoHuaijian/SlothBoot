package com.sloth.boot.starter.web.handler;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.common.exception.SystemException;
import com.sloth.boot.common.result.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler 测试")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private Locale originalLocale;

    @BeforeEach
    void setUp() {
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.CHINA);
        handler = new GlobalExceptionHandler();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        Locale.setDefault(originalLocale);
    }

    @Nested
    @DisplayName("业务异常处理")
    class BizExceptionTests {

        @Test
        @DisplayName("BizException 返回对应错误码和消息")
        void handleBizException() {
            BizException ex = BizException.of(GlobalErrorCode.NOT_FOUND, "用户不存在");
            R<Void> result = handler.handleBizException(ex);
            assertThat(result.getCode()).isEqualTo(404);
            assertThat(result.getMsg()).isEqualTo("用户不存在");
        }

        @Test
        @DisplayName("BizException 使用 ErrorCode 默认消息")
        void handleBizExceptionWithDefaultMsg() {
            BizException ex = BizException.of(GlobalErrorCode.UNAUTHORIZED);
            R<Void> result = handler.handleBizException(ex);
            assertThat(result.getCode()).isEqualTo(401);
            assertThat(result.getMsg()).isEqualTo("未认证");
        }
    }

    @Nested
    @DisplayName("系统异常处理")
    class SystemExceptionTests {

        @Test
        @DisplayName("SystemException 返回对应错误码和消息")
        void handleSystemException() {
            SystemException ex = SystemException.of(GlobalErrorCode.INTERNAL_ERROR, "数据库连接失败");
            R<Void> result = handler.handleSystemException(ex);
            assertThat(result.getCode()).isEqualTo(500);
            assertThat(result.getMsg()).isEqualTo("数据库连接失败");
        }
    }

    @Nested
    @DisplayName("参数校验异常处理")
    class ValidationExceptionTests {

        @Test
        @DisplayName("MissingServletRequestParameterException 返回 400")
        void handleMissingParam() {
            MissingServletRequestParameterException ex = new MissingServletRequestParameterException("id", "Long");
            R<Void> result = handler.handleMissingServletRequestParameterException(ex);
            assertThat(result.getCode()).isEqualTo(400);
            assertThat(result.getMsg()).contains("id");
        }

        @Test
        @DisplayName("HttpRequestMethodNotSupportedException 返回 405")
        void handleMethodNotAllowed() {
            HttpRequestMethodNotSupportedException ex =
                new HttpRequestMethodNotSupportedException("POST", List.of("GET"));
            R<Void> result = handler.handleHttpRequestMethodNotSupportedException(ex);
            assertThat(result.getCode()).isEqualTo(405);
        }

        @Test
        @DisplayName("NoHandlerFoundException 返回 404")
        void handleNoHandler() throws Exception {
            NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/unknown", null);
            R<Void> result = handler.handleNoHandlerFoundException(ex);
            assertThat(result.getCode()).isEqualTo(404);
            assertThat(result.getMsg()).isEqualTo("资源不存在");
        }

        @Test
        @DisplayName("MaxUploadSizeExceededException 返回 400")
        void handleMaxUploadSize() {
            MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1024);
            R<Void> result = handler.handleMaxUploadSizeExceededException(ex);
            assertThat(result.getCode()).isEqualTo(400);
            assertThat(result.getMsg()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("兜底异常处理")
    class CatchAllTests {

        @Test
        @DisplayName("未知异常返回 500")
        void handleUnknownException() {
            Exception ex = new RuntimeException("未知错误");
            R<Void> result = handler.handleException(ex);
            assertThat(result.getCode()).isEqualTo(500);
            assertThat(result.getMsg()).isEqualTo("系统内部异常");
        }
    }
}
