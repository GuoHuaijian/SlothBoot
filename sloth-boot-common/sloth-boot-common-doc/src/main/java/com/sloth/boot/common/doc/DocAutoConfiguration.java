package com.sloth.boot.common.doc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 接口文档自动配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@ConditionalOnProperty(prefix = "sloth.doc", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DocProperties.class)
public class DocAutoConfiguration {

    /**
     * 注册 OpenAPI 描述对象。
     *
     * @param docProperties 文档配置
     * @return OpenAPI 对象
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openApi(DocProperties docProperties) {
        return new OpenAPI().info(new Info()
                .title(docProperties.getTitle())
                .description(docProperties.getDescription())
                .version(docProperties.getVersion())
                .contact(new Contact()
                        .name(docProperties.getContactName())
                        .email(docProperties.getContactEmail())
                        .url(docProperties.getContactUrl()))
                .license(new License().name(docProperties.getLicense())));
    }

    /**
     * 注册接口分组。
     *
     * @param docProperties 文档配置
     * @return 分组文档对象
     */
    @Bean
    @ConditionalOnMissingBean
    public GroupedOpenApi groupedOpenApi(DocProperties docProperties) {
        List<String> basePackages = docProperties.getBasePackages();
        String[] packagesToScan = basePackages == null || basePackages.isEmpty()
                ? new String[]{"com.sloth.boot"}
                : basePackages.toArray(String[]::new);
        return GroupedOpenApi.builder()
                .group("default")
                .packagesToScan(packagesToScan)
                .pathsToMatch("/**")
                .build();
    }
}
