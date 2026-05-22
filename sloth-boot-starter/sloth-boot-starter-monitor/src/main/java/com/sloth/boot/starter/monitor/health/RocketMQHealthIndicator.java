package com.sloth.boot.starter.monitor.health;

import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;

import java.lang.reflect.Method;

public class RocketMQHealthIndicator extends AbstractHealthIndicator {

    private final Object rocketMQTemplate;

    public RocketMQHealthIndicator(Object rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Method getProducer = rocketMQTemplate.getClass().getMethod("getProducer");
        Object producer = getProducer.invoke(rocketMQTemplate);
        Method namesrvMethod = producer.getClass().getMethod("getNamesrvAddr");
        Method groupMethod = producer.getClass().getMethod("getProducerGroup");
        builder.up()
            .withDetail("namesrvAddr", String.valueOf(namesrvMethod.invoke(producer)))
            .withDetail("producerGroup", String.valueOf(groupMethod.invoke(producer)));
    }
}
