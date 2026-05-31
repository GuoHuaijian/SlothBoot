package com.sloth.boot.starter.rocketmq.config;

import com.sloth.boot.starter.rocketmq.dlq.DeadLetterQueueHandler;
import com.sloth.boot.starter.rocketmq.producer.MessageProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * RocketMQ 自动配置测试。
 */
class RocketMQAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(RocketMQAutoConfiguration.class))
        .withBean(RocketMQTemplate.class, () -> mock(RocketMQTemplate.class));

    @Test
    void should_register_producer_and_health_indicator() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MessageProducer.class);
            assertThat(context).hasSingleBean(RocketMQProperties.class);
        });
    }

    @Test
    void should_register_dlq_handler_when_enabled() {
        contextRunner
            .withPropertyValues("sloth.mq.dlq.enabled=true")
            .run(context -> assertThat(context).hasSingleBean(DeadLetterQueueHandler.class));
    }

    @Test
    void should_not_register_dlq_handler_when_disabled() {
        contextRunner
            .withPropertyValues("sloth.mq.dlq.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(DeadLetterQueueHandler.class));
    }

    @Test
    void should_not_register_when_property_disabled() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RocketMQAutoConfiguration.class))
            .withBean(RocketMQTemplate.class, () -> mock(RocketMQTemplate.class))
            .withPropertyValues("sloth.mq.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(MessageProducer.class));
    }
}
