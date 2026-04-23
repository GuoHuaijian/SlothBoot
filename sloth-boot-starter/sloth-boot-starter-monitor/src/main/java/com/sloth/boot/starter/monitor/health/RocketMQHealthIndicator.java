package com.sloth.boot.starter.monitor.health;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

import java.lang.reflect.Method;

/**
 * RocketMQ 健康检查器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class RocketMQHealthIndicator extends AbstractHealthIndicator {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 构造函数。
     *
     * @param rocketMQTemplate RocketMQTemplate
     */
    public RocketMQHealthIndicator(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    /**
     * 执行健康检查。
     *
     * @param builder Health 构建器
     * @throws Exception 异常
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Object producer = rocketMQTemplate.getProducer();
        Method namesrvMethod = producer.getClass().getMethod("getNamesrvAddr");
        Method groupMethod = producer.getClass().getMethod("getProducerGroup");
        builder.up()
                .withDetail("namesrvAddr", String.valueOf(namesrvMethod.invoke(producer)))
                .withDetail("producerGroup", String.valueOf(groupMethod.invoke(producer)));
    }
}
