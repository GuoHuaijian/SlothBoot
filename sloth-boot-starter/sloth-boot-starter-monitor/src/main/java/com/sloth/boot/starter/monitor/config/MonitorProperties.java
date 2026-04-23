package com.sloth.boot.starter.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 监控配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.monitor")
public class MonitorProperties {

    /**
     * 是否启用监控 starter。
     */
    private boolean enabled = true;

    /**
     * 是否启用慢接口监控。
     */
    private boolean slowApiEnabled = true;

    /**
     * 慢接口阈值，单位毫秒。
     */
    private long slowApiThreshold = 3000L;

    /**
     * 告警配置。
     */
    private Alarm alarm = new Alarm();

    /**
     * 告警配置对象。
     *
     * @author sloth-boot
     * @since 1.0.0
     */
    @Data
    public static class Alarm {

        /**
         * 是否启用告警。
         */
        private boolean enabled = false;

        /**
         * 告警类型。
         */
        private String type = "dingtalk";

        /**
         * Webhook 地址。
         */
        private String webhook;

        /**
         * Webhook 签名密钥。
         */
        private String secret;
    }
}
