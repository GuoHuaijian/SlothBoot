package com.sloth.boot.starter.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MQ 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.mq")
public class MQProperties {

    /**
     * 是否启用 MQ Starter。
     */
    private boolean enabled = true;

    /**
     * 是否启用消费幂等。
     */
    private boolean idempotentEnabled = true;

    /**
     * 最大重试次数。
     */
    private int maxRetry = 3;

    /**
     * 默认事务生产者组。
     */
    private String transactionProducerGroup = "sloth-tx-producer-group";

    /**
     * 消费幂等键前缀。
     */
    private String consumeIdempotentKeyPrefix = "sloth:mq:consume:";
}
