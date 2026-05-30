package com.sloth.boot.common.security.config;

import com.sloth.boot.common.security.desensitize.DesensitizeValueSerializerModifier;
import com.sloth.boot.common.security.sign.SignProperties;
import com.sloth.boot.common.security.xss.XssProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleModule;

/**
 * 安全模块自动配置。
 * <p>
 * 提供 XSS 防护、请求签名验证和数据脱敏的自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties({XssProperties.class, SignProperties.class})
public class SecurityAutoConfiguration {

    /**
     * 注册脱敏模块，自动检测 {@link com.sloth.boot.common.security.desensitize.Desensitize} 注解。
     *
     * @return Jackson 脱敏模块
     */
    @Bean
    public JacksonModule desensitizeModule() {
        SimpleModule module = new SimpleModule("sloth-desensitize");
        module.setSerializerModifier(new DesensitizeValueSerializerModifier());
        return module;
    }
}
