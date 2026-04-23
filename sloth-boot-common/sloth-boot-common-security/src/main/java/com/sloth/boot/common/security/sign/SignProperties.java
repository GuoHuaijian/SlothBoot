package com.sloth.boot.common.security.sign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * 签名配置属性
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.sign")
public class SignProperties {

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 有效时间（秒）
     */
    private int validTime = 300;

    /**
     * 排除的路径
     */
    private Set<String> excludePaths = new HashSet<>();

    /**
     * 是否启用签名验证
     */
    private boolean enabled = true;
}
