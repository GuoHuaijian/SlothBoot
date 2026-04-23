package com.sloth.boot.starter.feign.config;

import com.sloth.boot.starter.feign.decoder.FeignErrorDecoder;
import com.sloth.boot.starter.feign.decoder.FeignResponseDecoder;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Feign 自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignClient")
@Import({FeignLogConfig.class, OkHttpConfig.class})
public class FeignAutoConfiguration {

    /**
     * 注册请求拦截器。
     *
     * @return 请求拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }

    /**
     * 注册响应解码器。
     *
     * @param messageConverters 消息转换器工厂
     * @return 解码器
     */
    @Bean
    @ConditionalOnMissingBean(Decoder.class)
    public Decoder feignResponseDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        Decoder delegate = new ResponseEntityDecoder(new SpringDecoder(messageConverters));
        return new FeignResponseDecoder(delegate);
    }

    /**
     * 注册异常解码器。
     *
     * @return 异常解码器
     */
    @Bean
    @ConditionalOnMissingBean(ErrorDecoder.class)
    public ErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }
}
