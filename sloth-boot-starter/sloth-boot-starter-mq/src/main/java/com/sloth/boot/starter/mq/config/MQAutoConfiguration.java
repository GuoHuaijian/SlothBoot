package com.sloth.boot.starter.mq.config;

import com.sloth.boot.starter.mq.dlq.DeadLetterQueueHandler;
import com.sloth.boot.starter.mq.monitor.MQHealthIndicator;
import com.sloth.boot.starter.mq.producer.MessageProducer;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * MQ 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(RocketMQTemplate.class)
@ConditionalOnProperty(prefix = "sloth.mq", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MQProperties.class)
public class MQAutoConfiguration {

    /**
     * 注册消息生产者。
     *
     * @param rocketMQTemplate RocketMQTemplate
     * @param mqProperties     MQ 配置
     * @return 消息生产者
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageProducer messageProducer(RocketMQTemplate rocketMQTemplate, MQProperties mqProperties) {
        return new MessageProducer(rocketMQTemplate, mqProperties);
    }

    /**
     * 注册 MQ 健康检查。
     *
     * @param rocketMQTemplate RocketMQTemplate
     * @return 健康检查器
     */
    @Bean
    @ConditionalOnMissingBean(name = "slothMqHealthIndicator")
    @ConditionalOnClass(HealthIndicator.class)
    public MQHealthIndicator slothMqHealthIndicator(RocketMQTemplate rocketMQTemplate) {
        return new MQHealthIndicator(rocketMQTemplate);
    }

    // ==================== 新增特性 ====================

    /**
     * 注册消息消费失败日志监听器（监听 MessageFailureLogEvent）。
     *
     * @return 消息失败日志监听器
     */
    @Bean
    @ConditionalOnMissingBean(name = "messageFailureLogListener")
    public com.sloth.boot.starter.mq.event.MessageFailureLogListener messageFailureLogListener() {
        return new com.sloth.boot.starter.mq.event.MessageFailureLogListener();
    }

    /**
     * 注册死信队列处理器。
     *
     * @param messageProducer 消息生产者
     * @param mqProperties    MQ 配置
     * @return 死信队列处理器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.mq.dlq", name = "enabled", havingValue = "true")
    public DeadLetterQueueHandler deadLetterQueueHandler(MessageProducer messageProducer, MQProperties mqProperties) {
        return new DeadLetterQueueHandler(messageProducer, mqProperties);
    }
}
