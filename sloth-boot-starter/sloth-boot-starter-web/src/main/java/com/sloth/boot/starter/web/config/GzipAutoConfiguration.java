package com.sloth.boot.starter.web.config;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * Gzip 压缩自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(Http11NioProtocol.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "sloth.web.gzip", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(GzipProperties.class)
public class GzipAutoConfiguration {

    /**
     * 注册 Tomcat Gzip 压缩自定义器。
     *
     * @param gzipProperties Gzip 配置
     * @return Tomcat 服务器工厂自定义器
     */
    @Bean
    @ConditionalOnMissingBean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> gzipServerFactoryCustomizer(
        GzipProperties gzipProperties) {
        return factory -> factory.addConnectorCustomizers(connector -> {
            if (connector.getProtocolHandler() instanceof Http11NioProtocol protocol) {
                protocol.setCompression("on");
                protocol
                    .setCompressibleMimeType(StringUtils.arrayToCommaDelimitedString(gzipProperties.getMimeTypes()));
                protocol.setCompressionMinSize(gzipProperties.getMinSize());
            }
        });
    }
}
