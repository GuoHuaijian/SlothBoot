package com.sloth.boot.common.config;

import com.sloth.boot.common.util.SpringContextUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link SpringContextUtil} 自动配置。
 * <p>
 * 确保未对 {@code com.sloth.boot} 做组件扫描的消费者也能注册
 * {@link SpringContextUtil}，避免静态容器字段为空导致 NPE。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
public class SpringContextUtilAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }
}
