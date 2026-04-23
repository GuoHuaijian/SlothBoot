package com.sloth.boot.common.doc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 文档配置属性
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.doc")
public class DocProperties {

    private static final String DEFAULT_TITLE = "Sloth Boot API";
    private static final String DEFAULT_DESCRIPTION = "Sloth Boot 接口文档";
    private static final String DEFAULT_VERSION = "1.0.0";
    private static final String DEFAULT_CONTACT_NAME = "sloth-boot";
    private static final String DEFAULT_CONTACT_EMAIL = "sloth-boot@example.com";
    private static final String DEFAULT_CONTACT_URL = "https://github.com/your-github-id/sloth-boot";
    private static final String DEFAULT_LICENSE = "Apache 2.0";
    private static final List<String> DEFAULT_BASE_PACKAGES = List.of("com.sloth.boot");

    /**
     * 是否启用接口文档。
     */
    private boolean enabled = true;

    /**
     * 接口文档标题。
     */
    private String title = DEFAULT_TITLE;

    /**
     * 接口文档描述。
     */
    private String description = DEFAULT_DESCRIPTION;

    /**
     * 接口文档版本。
     */
    private String version = DEFAULT_VERSION;

    /**
     * 联系人名称。
     */
    private String contactName = DEFAULT_CONTACT_NAME;

    /**
     * 联系人邮箱。
     */
    private String contactEmail = DEFAULT_CONTACT_EMAIL;

    /**
     * 联系人主页或仓库地址。
     */
    private String contactUrl = DEFAULT_CONTACT_URL;

    /**
     * 开源许可证名称。
     */
    private String license = DEFAULT_LICENSE;

    /**
     * 扫描的基础包。
     */
    private List<String> basePackages = DEFAULT_BASE_PACKAGES;
}
