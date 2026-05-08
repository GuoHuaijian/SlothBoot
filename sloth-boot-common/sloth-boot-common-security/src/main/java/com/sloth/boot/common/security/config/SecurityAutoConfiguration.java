package com.sloth.boot.common.security.config;

import com.sloth.boot.common.security.sign.SignProperties;
import com.sloth.boot.common.security.xss.XssProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 安全模块自动配置。
 * <p>
 * 提供 XSS 防护和请求签名验证的自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties({XssProperties.class, SignProperties.class})
public class SecurityAutoConfiguration {

    /**
     * 注册 XSS 防护配置。
     *
     * @return XSS 配置
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.xss", name = "enabled", havingValue = "true", matchIfMissing = true)
    public XssProperties xssProperties() {
        return new XssProperties();
    }

    /**
     * 注册签名验证配置。
     *
     * @return 签名配置
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.sign", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SignProperties signProperties() {
        return new SignProperties();
    }
}
