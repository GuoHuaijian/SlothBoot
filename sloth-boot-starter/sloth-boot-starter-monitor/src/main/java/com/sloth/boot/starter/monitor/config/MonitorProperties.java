package com.sloth.boot.starter.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 监控配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
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
     * 健康检查配置。
     */
    private Health health = new Health();

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

        /**
         * CPU 使用率告警阈值（百分比），超过此值触发告警。
         */
        private double cpuThreshold = 80.0;

        /**
         * 内存使用率告警阈值（百分比），超过此值触发告警。
         */
        private double memoryThreshold = 80.0;

        /**
         * 磁盘使用率告警阈值（百分比），超过此值触发告警。
         */
        private double diskThreshold = 90.0;
    }

    /**
     * 健康检查配置。
     *
     * @author sloth-boot
     * @since 1.0.0
     */
    @Data
    public static class Health {

        /**
         * 健康检查 HTTP 连接超时，单位毫秒。
         */
        private int connectTimeout = 3000;

        /**
         * 健康检查 HTTP 读取超时，单位毫秒。
         */
        private int readTimeout = 3000;
    }
}
