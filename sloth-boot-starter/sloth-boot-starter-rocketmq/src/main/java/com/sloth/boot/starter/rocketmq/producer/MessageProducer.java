package com.sloth.boot.starter.rocketmq.producer;

import com.sloth.boot.common.exception.SystemException;
import com.sloth.boot.starter.rocketmq.config.RocketMQProperties;
import com.sloth.boot.starter.rocketmq.interceptor.TraceMessageInterceptor;
import com.sloth.boot.starter.rocketmq.model.BaseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;

import java.lang.reflect.Method;

/**
 * RocketMQ 消息生产者。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class MessageProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final RocketMQProperties mqProperties;

    /**
     * 同步发送消息。
     *
     * @param topic 主题
     * @param tag   标签
     * @param msg   消息体
     * @return 发送结果
     */
    public SendResult sendSync(String topic, String tag, BaseMessage msg) {
        String destination = buildDestination(topic, tag);
        Message<String> message = TraceMessageInterceptor.buildMessage(msg);
        log.info("[MQ] 同步发送 RocketMQ 消息, destination={}, msgId={}", destination, msg.getMsgId());
        SendResult result = rocketMQTemplate.syncSend(destination, message);
        log.info("[MQ] RocketMQ 同步发送完成, destination={}, result={}", destination, result);
        return result;
    }

    /**
     * 同步发送消息。
     *
     * @param topic 主题
     * @param msg   消息体
     * @return 发送结果
     */
    public SendResult sendSync(String topic, BaseMessage msg) {
        return sendSync(topic, null, msg);
    }

    /**
     * 异步发送消息。
     *
     * @param topic    主题
     * @param tag      标签
     * @param msg      消息体
     * @param callback 回调
     */
    public void sendAsync(String topic, String tag, BaseMessage msg, SendCallback callback) {
        String destination = buildDestination(topic, tag);
        Message<String> message = TraceMessageInterceptor.buildMessage(msg);
        log.info("[MQ] 异步发送 RocketMQ 消息, destination={}, msgId={}", destination, msg.getMsgId());
        rocketMQTemplate.asyncSend(destination, message, callback);
    }

    /**
     * 单向发送消息。
     *
     * @param topic 主题
     * @param msg   消息体
     */
    public void sendOneway(String topic, BaseMessage msg) {
        Message<String> message = TraceMessageInterceptor.buildMessage(msg);
        log.info("[MQ] 单向发送 RocketMQ 消息, topic={}, msgId={}", topic, msg.getMsgId());
        rocketMQTemplate.sendOneWay(topic, message);
    }

    /**
     * 发送延迟消息。
     *
     * @param topic      主题
     * @param msg        消息体
     * @param delayLevel 延迟级别
     * @return 发送结果
     */
    public SendResult sendDelay(String topic, BaseMessage msg, int delayLevel) {
        Message<String> message = TraceMessageInterceptor.buildMessage(msg);
        log.info("[MQ] 发送延迟 RocketMQ 消息, topic={}, delayLevel={}, msgId={}",
            topic, delayLevel, msg.getMsgId());
        return rocketMQTemplate.syncSend(topic, message, rocketMQTemplate.getProducer().getSendMsgTimeout(), delayLevel);
    }

    /**
     * 顺序发送消息。
     *
     * @param topic   主题
     * @param msg     消息体
     * @param hashKey 顺序键
     * @return 发送结果
     */
    public SendResult sendOrderly(String topic, BaseMessage msg, String hashKey) {
        Message<String> message = TraceMessageInterceptor.buildMessage(msg);
        log.info("[MQ] 顺序发送 RocketMQ 消息, topic={}, hashKey={}, msgId={}",
            topic, hashKey, msg.getMsgId());
        return rocketMQTemplate.syncSendOrderly(topic, message, hashKey);
    }

    /**
     * 发送事务消息。
     *
     * @param topic 主题
     * @param msg   消息体
     * @param arg   扩展参数
     * @return 发送结果
     */
    public TransactionSendResult sendTransaction(String topic, BaseMessage msg, Object arg) {
        Message<String> message = TraceMessageInterceptor.buildMessage(msg);
        log.info("[MQ] 发送事务 RocketMQ 消息, topic={}, msgId={}", topic, msg.getMsgId());
        return invokeTransactionSend(topic, message, arg);
    }

    private String buildDestination(String topic, String tag) {
        if (tag == null || tag.isBlank()) {
            return topic;
        }
        return topic + ":" + tag;
    }

    private TransactionSendResult invokeTransactionSend(String topic, Message<String> message, Object arg) {
        try {
            Method method = RocketMQTemplate.class.getMethod(
                "sendMessageInTransaction",
                String.class,
                String.class,
                Message.class,
                Object.class
            );
            return (TransactionSendResult) method.invoke(
                rocketMQTemplate,
                mqProperties.getTransactionProducerGroup(),
                topic,
                message,
                arg
            );
        } catch (NoSuchMethodException ex) {
            try {
                Method method = RocketMQTemplate.class.getMethod(
                    "sendMessageInTransaction",
                    String.class,
                    Message.class,
                    Object.class
                );
                return (TransactionSendResult) method.invoke(rocketMQTemplate, topic, message, arg);
            } catch (ReflectiveOperationException innerEx) {
                throw SystemException.of("发送事务消息失败", innerEx);
            }
        } catch (ReflectiveOperationException ex) {
            throw SystemException.of("发送事务消息失败", ex);
        }
    }
}
