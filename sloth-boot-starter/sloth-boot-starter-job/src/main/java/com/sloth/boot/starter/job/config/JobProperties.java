package com.sloth.boot.starter.job.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * XXL-Job 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sloth.job")
public class JobProperties {

    /**
     * 是否启用 Job Starter。
     */
    private boolean enabled = true;

    private String adminAddresses;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String accessToken;
    private String appname;
    private String address;
    private String ip;
    private int port = 9999;
    private String logPath = "./logs/xxl-job";
    private int logRetentionDays = 30;
}
