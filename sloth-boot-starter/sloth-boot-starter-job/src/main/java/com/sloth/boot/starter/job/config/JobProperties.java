package com.sloth.boot.starter.job.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * XXL-Job 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.job")
public class JobProperties {

    private String adminAddresses;
    private String accessToken;
    private String appname;
    private String address;
    private String ip;
    private int port = 9999;
    private String logPath = "./logs/xxl-job";
    private int logRetentionDays = 30;
}
