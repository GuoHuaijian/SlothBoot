package com.sloth.boot.common.doc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口文档自动配置。
 * <p>
 * 提供 OpenAPI 描述、Bearer Token 安全方案、服务器地址自动检测、多分组支持。
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
     * @param environment   Spring 环境
     * @return OpenAPI 对象
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openApi(DocProperties docProperties, Environment environment) {
        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title(docProperties.getTitle())
                .description(docProperties.getDescription())
                .version(docProperties.getVersion())
                .contact(new Contact()
                    .name(docProperties.getContactName())
                    .email(docProperties.getContactEmail())
                    .url(docProperties.getContactUrl()))
                .license(new License().name(docProperties.getLicense())));

        // 添加 Bearer Token 安全方案
        if (docProperties.isSecuritySchemeEnabled()) {
            String schemeName = docProperties.getSecuritySchemeName();
            openAPI
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                    .addSecuritySchemes(schemeName, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat(docProperties.getSecurityBearerFormat())
                        .description(docProperties.getSecurityDescription())));
        }

        // 添加服务器地址
        List<Server> servers = buildServers(docProperties, environment);
        if (!servers.isEmpty()) {
            openAPI.servers(servers);
        }

        return openAPI;
    }

    /**
     * 注册默认接口分组。
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

    /**
     * 注册自定义分组（根据配置动态创建）。
     *
     * @param docProperties 文档配置
     * @return 自定义分组列表
     */
    @Bean
    @ConditionalOnMissingBean(name = "customGroupedOpenApis")
    public List<GroupedOpenApi> customGroupedOpenApis(DocProperties docProperties) {
        List<DocProperties.ApiGroup> groups = docProperties.getGroups();
        if (CollectionUtils.isEmpty(groups)) {
            return List.of();
        }
        List<GroupedOpenApi> apis = new ArrayList<>();
        for (DocProperties.ApiGroup group : groups) {
            GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
                .group(group.getName());
            if (!CollectionUtils.isEmpty(group.getPaths())) {
                builder.pathsToMatch(group.getPaths().toArray(String[]::new));
            }
            if (!CollectionUtils.isEmpty(group.getPackages())) {
                builder.packagesToScan(group.getPackages().toArray(String[]::new));
            }
            apis.add(builder.build());
        }
        return apis;
    }

    /**
     * 构建服务器地址列表。优先使用显式配置的 URL，否则自动检测。
     *
     * @param docProperties 文档配置
     * @param environment   Spring 环境
     * @return 服务器地址列表
     */
    private List<Server> buildServers(DocProperties docProperties, Environment environment) {
        List<Server> servers = new ArrayList<>();

        // 优先使用显式配置的 URL
        if (StringUtils.hasText(docProperties.getServerUrl())) {
            servers.add(new Server()
                .url(docProperties.getServerUrl())
                .description(docProperties.getServerDescription()));
            return servers;
        }

        // 自动检测
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            String port = environment.getProperty("server.port", "8080");
            String contextPath = environment.getProperty("server.servlet.context-path", "");
            if (contextPath.endsWith("/")) {
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }
            String url = "http://" + host + ":" + port + contextPath;
            servers.add(new Server().url(url).description("自动检测"));
        } catch (Exception ignored) {
            // 自动检测失败时忽略
        }

        return servers;
    }
}
