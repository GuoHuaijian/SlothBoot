package com.sloth.boot.starter.web.handler;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.exception.BaseException;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.common.exception.SystemException;
import com.sloth.boot.common.result.R;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     *
     * @param ex 业务异常
     * @return 统一响应
     */
    @ExceptionHandler(BizException.class)
    public R<Void> handleBizException(BizException ex) {
        log.warn("业务异常, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
        return buildBaseExceptionResponse(ex);
    }

    /**
     * 处理系统异常。
     *
     * @param ex 系统异常
     * @return 统一响应
     */
    @ExceptionHandler(SystemException.class)
    public R<Void> handleSystemException(SystemException ex) {
        log.error("系统异常, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
        return buildBaseExceptionResponse(ex);
    }

    /**
     * 处理参数校验异常。
     *
     * @param ex 参数校验异常
     * @return 统一响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleBadRequest(ex, ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; ")));
    }

    /**
     * 处理绑定异常。
     *
     * @param ex 绑定异常
     * @return 统一响应
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException ex) {
        return handleBadRequest(ex, ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; ")));
    }

    /**
     * 处理约束异常。
     *
     * @param ex 约束异常
     * @return 统一响应
     */
    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    public R<Void> handleConstraintViolationException(Exception ex) {
        String message = ex instanceof ConstraintViolationException constraintViolationException
                ? constraintViolationException.getConstraintViolations().stream()
                .map(item -> item.getPropertyPath() + " " + item.getMessage())
                .collect(Collectors.joining("; "))
                : ex.getMessage();
        return handleBadRequest(ex, message);
    }

    /**
     * 处理缺少请求参数异常。
     *
     * @param ex 缺少请求参数异常
     * @return 统一响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return handleBadRequest(ex, "缺少请求参数: " + ex.getParameterName());
    }

    /**
     * 处理不支持的请求方法异常。
     *
     * @param ex 请求方法异常
     * @return 统一响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("请求方法不支持, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
        return R.fail(GlobalErrorCode.METHOD_NOT_ALLOWED.getCode(), ex.getMessage());
    }

    /**
     * 处理不支持的媒体类型异常。
     *
     * @param ex 媒体类型异常
     * @return 统一响应
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public R<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.warn("媒体类型不支持, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
        return R.fail(415, "不支持的媒体类型");
    }

    /**
     * 处理请求路径不存在异常。
     *
     * @param ex 路径不存在异常
     * @return 统一响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("请求路径不存在, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
        return R.fail(GlobalErrorCode.NOT_FOUND);
    }

    /**
     * 处理请求体不可读异常。
     *
     * @param ex 请求体异常
     * @return 统一响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return handleBadRequest(ex, "请求体解析失败");
    }

    /**
     * 处理上传文件过大异常。
     *
     * @param ex 上传异常
     * @return 统一响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.warn("上传文件过大, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
        return R.fail(GlobalErrorCode.BAD_REQUEST.getCode(), "上传文件过大");
    }

    /**
     * 处理兜底异常。
     *
     * @param ex 未捕获异常
     * @return 统一响应
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception ex) {
        if (isDuplicateKeyException(ex)) {
            log.warn("数据重复, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
            return R.fail(GlobalErrorCode.BAD_REQUEST.getCode(), "数据已存在");
        }
        if (isAccessDeniedException(ex)) {
            log.warn("权限不足, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
            return R.fail(GlobalErrorCode.FORBIDDEN);
        }
        if (isBlockException(ex)) {
            log.warn("Sentinel 限流/降级触发, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
            return buildBlockExceptionResponse(ex);
        }
        log.error("系统兜底异常, traceId={}, msg={}", TraceContext.getTraceId(), ex.getMessage(), ex);
        return R.fail(GlobalErrorCode.INTERNAL_ERROR);
    }

    private R<Void> buildBaseExceptionResponse(BaseException ex) {
        return R.fail(ex.getCode(), ex.getMessage());
    }

    private R<Void> handleBadRequest(Exception ex, String message) {
        log.warn("请求参数异常, traceId={}, msg={}", TraceContext.getTraceId(), message, ex);
        return R.fail(GlobalErrorCode.BAD_REQUEST.getCode(), StrUtil.blankToDefault(message, GlobalErrorCode.BAD_REQUEST.getMsg()));
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private boolean isDuplicateKeyException(Throwable throwable) {
        return hasExceptionName(throwable, "org.springframework.dao.DuplicateKeyException");
    }

    private boolean isAccessDeniedException(Throwable throwable) {
        return hasExceptionName(throwable, "org.springframework.security.access.AccessDeniedException");
    }

    private boolean isBlockException(Throwable throwable) {
        return hasExceptionName(throwable, "com.alibaba.csp.sentinel.slots.block.BlockException");
    }

    private boolean hasExceptionName(Throwable throwable, String className) {
        Throwable current = throwable;
        while (current != null) {
            Class<?> type = current.getClass();
            while (type != null) {
                if (className.equals(type.getName())) {
                    return true;
                }
                type = type.getSuperclass();
            }
            current = current.getCause();
        }
        return false;
    }

    private R<Void> buildBlockExceptionResponse(Throwable throwable) {
        String exceptionName = throwable.getClass().getName();
        if ("com.alibaba.csp.sentinel.slots.block.flow.FlowException".equals(exceptionName)
                || "com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException".equals(exceptionName)) {
            return R.fail(GlobalErrorCode.TOO_MANY_REQUESTS.getCode(), "请求过于频繁，请稍后再试");
        }
        if ("com.alibaba.csp.sentinel.slots.block.degrade.DegradeException".equals(exceptionName)
                || "com.alibaba.csp.sentinel.slots.system.SystemBlockException".equals(exceptionName)) {
            return R.fail(503, "服务暂时不可用，请稍后再试");
        }
        if ("com.alibaba.csp.sentinel.slots.block.authority.AuthorityException".equals(exceptionName)) {
            return R.fail(GlobalErrorCode.FORBIDDEN.getCode(), "授权规则不通过");
        }
        return R.fail(GlobalErrorCode.TOO_MANY_REQUESTS.getCode(), "请求被限流，请稍后重试");
    }
}
