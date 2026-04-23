package com.sloth.boot.starter.mq.interceptor;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.mq.model.BaseMessage;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.MDC;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;

/**
 * 消息链路追踪处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class TraceMessageInterceptor {

    private TraceMessageInterceptor() {
    }

    /**
     * 构建带链路信息的 Spring 消息对象。
     *
     * @param message 业务消息
     * @return Spring 消息对象
     */
    public static Message<String> buildMessage(BaseMessage message) {
        String traceId = message.getTraceId();
        if (traceId == null || traceId.isBlank()) {
            traceId = TraceContext.generateTraceId();
            message.setTraceId(traceId);
        }
        message.getHeaders().putIfAbsent(HeaderConstant.TRACE_ID, traceId);
        message.getHeaders().putIfAbsent("msgId", message.getMsgId());
        if (message.getBizKey() != null) {
            message.getHeaders().putIfAbsent("bizKey", message.getBizKey());
        }
        MessageBuilder<String> builder = MessageBuilder.withPayload(JsonUtil.toJson(message))
                .setHeader(HeaderConstant.TRACE_ID, traceId)
                .setHeader("msgId", message.getMsgId());
        if (message.getBizKey() != null) {
            builder.setHeader("bizKey", message.getBizKey());
        }
        message.getHeaders().forEach(builder::setHeader);
        return builder.build();
    }

    /**
     * 从消费消息中恢复 TraceId。
     *
     * @param messageExt RocketMQ 消息
     * @return TraceId
     */
    public static String restoreTrace(MessageExt messageExt) {
        String traceId = messageExt.getUserProperty(HeaderConstant.TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            BaseMessage baseMessage = JsonUtil.parseObject(
                    new String(messageExt.getBody(), StandardCharsets.UTF_8),
                    BaseMessage.class
            );
            traceId = baseMessage.getTraceId();
        }
        if (traceId == null || traceId.isBlank()) {
            traceId = TraceContext.generateTraceId();
        }
        TraceContext.TraceInfo traceInfo = new TraceContext.TraceInfo();
        traceInfo.setTraceId(traceId);
        TraceContext.set(traceInfo);
        MDC.put("traceId", traceId);
        return traceId;
    }

    /**
     * 清理 Trace 上下文。
     */
    public static void clearTrace() {
        MDC.remove("traceId");
        TraceContext.clear();
    }
}
