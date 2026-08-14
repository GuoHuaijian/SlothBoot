package com.sloth.boot.starter.sms.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String accessKeyId;

    /**
     * 访问 Key Secret。
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
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
