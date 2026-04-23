package com.sloth.boot.starter.mq.monitor;

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
public class MQHealthIndicator extends AbstractHealthIndicator {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 构造函数。
     *
     * @param rocketMQTemplate RocketMQTemplate
     */
    public MQHealthIndicator(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    /**
     * 执行健康检查。
     *
     * @param builder Health 构建器
     * @throws Exception 检查异常
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Object producer = rocketMQTemplate.getProducer();
        String namesrvAddr = invokeString(producer, "getNamesrvAddr");
        String producerGroup = invokeString(producer, "getProducerGroup");
        String serviceState = invokeToString(producer, "getDefaultMQProducerImpl", "getServiceState");
        builder.up()
                .withDetail("namesrvAddr", namesrvAddr)
                .withDetail("producerGroup", producerGroup)
                .withDetail("serviceState", serviceState);
    }

    private String invokeString(Object target, String methodName) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        Object result = method.invoke(target);
        return result == null ? null : result.toString();
    }

    private String invokeToString(Object target, String firstMethod, String secondMethod) throws Exception {
        Method method = target.getClass().getMethod(firstMethod);
        Object first = method.invoke(target);
        if (first == null) {
            return null;
        }
        Method second = first.getClass().getMethod(secondMethod);
        Object result = second.invoke(first);
        return result == null ? null : result.toString();
    }
}
