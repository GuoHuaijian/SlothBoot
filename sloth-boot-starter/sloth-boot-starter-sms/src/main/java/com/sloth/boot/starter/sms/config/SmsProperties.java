package com.sloth.boot.starter.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 短信配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.sms")
public class SmsProperties {

    /**
     * 是否启用。
     */
    private boolean enabled = true;

    /**
     * 短信供应商类型。
     */
    private String type = "aliyun";

    /**
     * 访问 Key Id。
     */
    private String accessKeyId;

    /**
     * 访问 Key Secret。
     */
    private String accessKeySecret;

    /**
     * 短信签名。
     */
    private String signName;

    /**
     * 区域 ID。
     */
    private String regionId = "cn-hangzhou";
}
