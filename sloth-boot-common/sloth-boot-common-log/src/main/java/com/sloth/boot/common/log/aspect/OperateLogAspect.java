package com.sloth.boot.common.log.aspect;

import com.sloth.boot.common.annotation.OperateLog;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.log.event.OperateLogEvent;
import com.sloth.boot.common.log.model.OperateLogDTO;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.common.util.ServletUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Aspect
@RequiredArgsConstructor
public class OperateLogAspect {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 采集操作日志。
     *
     * @param joinPoint  切点
     * @param operateLog 操作日志注解
     * @return 方法结果
     * @throws Throwable 执行异常
     */
    @Around("@annotation(operateLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperateLog operateLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = ServletUtil.getRequest();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        OperateLogDTO dto = new OperateLogDTO();
        dto.setTraceId(TraceContext.getTraceId());
        dto.setModule(operateLog.module());
        dto.setDescription(operateLog.description());
        dto.setOperateType(operateLog.type().name());
        dto.setMethod(method.getDeclaringClass().getName() + "#" + method.getName());
        if (request != null) {
            dto.setRequestUrl(request.getRequestURI());
            dto.setRequestMethod(request.getMethod());
            dto.setOperatorIp(ServletUtil.getClientIp(request));
            dto.setUserAgent(request.getHeader("User-Agent"));
        }
        dto.setOperatorId(UserContext.getUserId() == null ? null : String.valueOf(UserContext.getUserId()));
        dto.setOperatorName(UserContext.getUsername());
        dto.setOperateTime(LocalDateTime.now());

        if (operateLog.saveRequestData()) {
            dto.setRequestParams(JsonUtil.toJson(joinPoint.getArgs()));
        }

        Object result = null;
        try {
            result = joinPoint.proceed();
            dto.setStatus(0);
            return result;
        } catch (Throwable throwable) {
            dto.setStatus(1);
            dto.setErrorMsg(throwable.getMessage());
            throw throwable;
        } finally {
            dto.setCostTime(System.currentTimeMillis() - startTime);
            if (operateLog.saveResponseData() && result != null) {
                dto.setResponseResult(JsonUtil.toJson(result));
            }
            eventPublisher.publishEvent(new OperateLogEvent(dto));
        }
    }
}
