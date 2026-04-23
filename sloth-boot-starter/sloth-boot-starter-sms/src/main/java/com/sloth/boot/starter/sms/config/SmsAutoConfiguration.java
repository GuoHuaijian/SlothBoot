package com.sloth.boot.starter.sms.config;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.starter.sms.core.AliyunSmsClient;
import com.sloth.boot.starter.sms.core.SmsClient;
import com.sloth.boot.starter.sms.core.SmsTemplate;
import com.sloth.boot.starter.sms.core.TencentSmsClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 短信自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "sloth.sms", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration {

    /**
     * 注册短信客户端。
     *
     * @param smsProperties 配置属性
     * @return 短信客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsClient smsClient(SmsProperties smsProperties) {
        if (StrUtil.equalsIgnoreCase("tencent", smsProperties.getType())) {
            return new TencentSmsClient();
        }
        return new AliyunSmsClient(smsProperties);
    }

    /**
     * 注册短信模板。
     *
     * @param smsClient 短信客户端
     * @return 短信模板
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsTemplate smsTemplate(SmsClient smsClient) {
        return new SmsTemplate(smsClient);
    }
}
