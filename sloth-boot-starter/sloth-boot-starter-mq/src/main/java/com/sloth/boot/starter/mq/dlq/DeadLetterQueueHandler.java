package com.sloth.boot.starter.mq.dlq;

import com.sloth.boot.starter.mq.config.MQProperties;
import com.sloth.boot.starter.mq.model.BaseMessage;
import com.sloth.boot.starter.mq.producer.MessageProducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 死信队列处理器。
 * <p>
 * 提供消息消费失败后的重试逻辑，达到最大重试次数后将消息投递到 DLQ 主题。 通过 {@code sloth.mq.dlq.enabled=true}
 * 启用。
 * </p>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnProperty(prefix = "sloth.mq.dlq", name = "enabled", havingValue = "true")
public class DeadLetterQueueHandler {

    private static final String DLQ_SUFFIX = "-DLQ";

    private final MessageProducer messageProducer;
    private final MQProperties mqProperties;
    private final ScheduledExecutorService scheduler;

    /**
     * 重试计数器（key: msgId）。
     */
    private final Map<String, Integer> retryCounters = new ConcurrentHashMap<>();

    /**
     * 构造函数。
     *
     * @param messageProducer 消息生产者
     * @param mqProperties    MQ 配置
     */
    public DeadLetterQueueHandler(MessageProducer messageProducer, MQProperties mqProperties) {
        this.messageProducer = messageProducer;
        this.mqProperties = mqProperties;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sloth-dlq-retry");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * 处理消费失败的消息，进行延迟重试或投递到死信队列。
     *
     * @param originalTopic 原始主题
     * @param message       消费失败的消息
     * @param cause         失败原因
     */
    public void handleFailure(String originalTopic, BaseMessage message, Throwable cause) {
        MQProperties.DLQProperties dlq = mqProperties.getDlq();
        String msgId = message.getMsgId();
        int currentRetry = retryCounters.merge(msgId, 1, Integer::sum);
        int maxRetries = dlq.getMaxRetries();

        log.warn("DLQ 处理消费失败消息, topic={}, msgId={}, currentRetry={}, maxRetries={}", originalTopic, msgId, currentRetry,
            maxRetries);

        if (currentRetry > maxRetries) {
            publishToDlq(originalTopic, message, cause);
            retryCounters.remove(msgId);
        } else {
            scheduleRetry(originalTopic, message, dlq.getDelaySeconds());
        }
    }

    /**
     * 将消息投递到死信队列主题。
     *
     * @param originalTopic 原始主题
     * @param message       消息
     * @param cause         失败原因
     */
    private void publishToDlq(String originalTopic, BaseMessage message, Throwable cause) {
        String dlqTopic = originalTopic + DLQ_SUFFIX;
        message.getHeaders().put("dlq-original-topic", originalTopic);
        message.getHeaders().put("dlq-failure-reason", cause.getMessage());
        message.getHeaders().put("dlq-retry-count", String.valueOf(mqProperties.getDlq().getMaxRetries()));

        try {
            SendResult result = messageProducer.sendSync(dlqTopic, null, message);
            if (result.getSendStatus() == SendStatus.SEND_OK) {
                log.info("消息已投递到死信队列, dlqTopic={}, msgId={}", dlqTopic, message.getMsgId());
            } else {
                log.error("消息投递死信队列失败, dlqTopic={}, msgId={}, status={}", dlqTopic, message.getMsgId(),
                    result.getSendStatus());
            }
        } catch (Exception ex) {
            log.error("消息投递死信队列异常, dlqTopic={}, msgId={}", dlqTopic, message.getMsgId(), ex);
        }
    }

    /**
     * 调度延迟重试。
     *
     * @param topic        原始主题
     * @param message      消息
     * @param delaySeconds 延迟秒数
     */
    private void scheduleRetry(String topic, BaseMessage message, int delaySeconds) {
        log.info("调度延迟重试, topic={}, msgId={}, delaySeconds={}", topic, message.getMsgId(), delaySeconds);
        scheduler.schedule(() -> {
            try {
                messageProducer.sendDelay(topic, message, delaySecondsToDelayLevel(delaySeconds));
            } catch (Exception ex) {
                log.error("延迟重试发送失败, topic={}, msgId={}", topic, message.getMsgId(), ex);
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * 将延迟秒数映射到 RocketMQ 延迟级别。
     * <p>
     * RocketMQ 延迟级别：1=1s, 2=5s, 3=10s, 4=30s, 5=1m, 6=2m, 7=3m, 8=4m, 9=5m, 10=6m,
     * 11=7m, 12=8m, 13=9m, 14=10m, 15=20m, 16=30m
     * </p>
     *
     * @param delaySeconds 延迟秒数
     * @return 延迟级别
     */
    private int delaySecondsToDelayLevel(int delaySeconds) {
        if (delaySeconds <= 1) {
            return 1;
        } else if (delaySeconds <= 5) {
            return 2;
        } else if (delaySeconds <= 10) {
            return 3;
        } else if (delaySeconds <= 30) {
            return 4;
        } else if (delaySeconds <= 60) {
            return 5;
        } else if (delaySeconds <= 120) {
            return 6;
        } else if (delaySeconds <= 180) {
            return 7;
        } else if (delaySeconds <= 300) {
            return 9;
        } else if (delaySeconds <= 600) {
            return 12;
        } else if (delaySeconds <= 1800) {
            return 16;
        } else {
            return 16;
        }
    }

    /**
     * 销毁时关闭调度器。
     */
    public void destroy() {
        scheduler.shutdown();
    }
}
