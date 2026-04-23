package com.sloth.boot.starter.mq.consumer;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.mq.config.MQProperties;
import com.sloth.boot.starter.mq.interceptor.TraceMessageInterceptor;
import com.sloth.boot.starter.mq.model.BaseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 消息监听抽象基类。
 *
 * @param <T> 消息类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractMessageListener<T extends BaseMessage> implements RocketMQListener<MessageExt> {

    private final MQProperties mqProperties;
    private final ObjectProvider<StringRedisTemplate> stringRedisTemplateProvider;

    /**
     * 构造函数。
     *
     * @param mqProperties               MQ 配置
     * @param stringRedisTemplateProvider Redis 模板提供者
     */
    protected AbstractMessageListener(MQProperties mqProperties,
                                      ObjectProvider<StringRedisTemplate> stringRedisTemplateProvider) {
        this.mqProperties = mqProperties;
        this.stringRedisTemplateProvider = stringRedisTemplateProvider;
    }

    /**
     * RocketMQ 回调入口。
     *
     * @param messageExt 原始消息
     */
    @Override
    public final void onMessage(MessageExt messageExt) {
        long startTime = System.currentTimeMillis();
        String traceId = TraceMessageInterceptor.restoreTrace(messageExt);
        try {
            T message = parseMessage(messageExt);
            if (needIdempotentCheck(message) && !markConsumed(message)) {
                log.warn("RocketMQ 消息重复消费, topic={}, msgId={}, traceId={}",
                        messageExt.getTopic(), message.getMsgId(), traceId);
                return;
            }
            onMessage(message);
            log.info("RocketMQ 消息消费成功, topic={}, msgId={}, traceId={}, cost={}ms",
                    messageExt.getTopic(), message.getMsgId(), traceId, System.currentTimeMillis() - startTime);
        } catch (Exception ex) {
            int reconsumeTimes = messageExt.getReconsumeTimes();
            log.error("RocketMQ 消息消费失败, topic={}, msgId={}, traceId={}, retryTimes={}, maxRetry={}, cost={}ms",
                    messageExt.getTopic(),
                    messageExt.getMsgId(),
                    traceId,
                    reconsumeTimes,
                    mqProperties.getMaxRetry(),
                    System.currentTimeMillis() - startTime,
                    ex);
            if (reconsumeTimes >= mqProperties.getMaxRetry()) {
                log.error("RocketMQ 消息达到最大重试阈值, topic={}, msgId={}, traceId={}, retryTimes={}",
                        messageExt.getTopic(), messageExt.getMsgId(), traceId, reconsumeTimes);
            }
            throw ex;
        } finally {
            TraceMessageInterceptor.clearTrace();
        }
    }

    /**
     * 业务模板方法。
     *
     * @param message 业务消息
     */
    protected abstract void onMessage(T message);

    private T parseMessage(MessageExt messageExt) {
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        T message = JsonUtil.parseObject(body, resolveMessageClass());
        if (message.getMsgId() == null) {
            message.setMsgId(messageExt.getMsgId());
        }
        if (message.getTraceId() == null || message.getTraceId().isBlank()) {
            message.setTraceId(messageExt.getUserProperty(HeaderConstant.TRACE_ID));
        }
        return message;
    }

    @SuppressWarnings("unchecked")
    private Class<T> resolveMessageClass() {
        Class<?> currentClass = getClass();
        Type genericSuperclass = currentClass.getGenericSuperclass();
        while (!(genericSuperclass instanceof ParameterizedType) && currentClass.getSuperclass() != null) {
            currentClass = currentClass.getSuperclass();
            genericSuperclass = currentClass.getGenericSuperclass();
        }
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            return (Class<T>) parameterizedType.getActualTypeArguments()[0];
        }
        return (Class<T>) BaseMessage.class;
    }

    private boolean needIdempotentCheck(T message) {
        return mqProperties.isIdempotentEnabled() && message != null && message.getMsgId() != null;
    }

    private boolean markConsumed(T message) {
        StringRedisTemplate stringRedisTemplate = stringRedisTemplateProvider.getIfAvailable();
        if (stringRedisTemplate == null) {
            return true;
        }
        String key = mqProperties.getConsumeIdempotentKeyPrefix() + message.getMsgId();
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofDays(1));
        return Boolean.TRUE.equals(success);
    }
}
