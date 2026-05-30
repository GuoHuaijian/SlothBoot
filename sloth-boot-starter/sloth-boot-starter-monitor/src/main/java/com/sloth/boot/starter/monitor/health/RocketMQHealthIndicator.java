package com.sloth.boot.starter.monitor.health;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;

/**
 * RocketMQ 健康检查。
 * <p>
 * 检查生产者连接状态和 NameServer 地址。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RocketMQHealthIndicator extends AbstractHealthIndicator {

    private final RocketMQTemplate rocketMQTemplate;

    public RocketMQHealthIndicator(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            DefaultMQProducer producer = rocketMQTemplate.getProducer();
            builder.up()
                .withDetail("namesrvAddr", producer.getNamesrvAddr())
                .withDetail("producerGroup", producer.getProducerGroup());
        } catch (Exception e) {
            builder.down(e);
        }
    }
}
