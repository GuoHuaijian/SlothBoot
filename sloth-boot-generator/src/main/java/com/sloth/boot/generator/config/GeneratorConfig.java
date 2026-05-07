package com.sloth.boot.generator.config;

import lombok.Data;

/**
 * 代码生成器配置。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class GeneratorConfig {

    // ==================== 数据库配置 ====================

    /**
     * 数据库驱动
     */
    private String driver = "com.mysql.cj.jdbc.Driver";

    /**
     * 数据库 URL
     */
    private String url = "jdbc:mysql://127.0.0.1:3306/sloth_boot?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";

    /**
     * 数据库用户名
     */
    private String username = "root";

    /**
     * 数据库密码
     */
    private String password = "root";

    // ==================== 包配置 ====================

    /**
     * 父包名
     */
    private String parentPackage = "com.sloth.boot";

    /**
     * 模块名（生成在 parentPackage 下）
     */
    private String moduleName = "system";

    /**
     * Entity 包名
     */
    private String entityPackage = "domain.entity";

    /**
     * Mapper 包名
     */
    private String mapperPackage = "mapper";

    /**
     * Service 包名
     */
    private String servicePackage = "service";

    /**
     * Service 实现类包名
     */
    private String serviceImplPackage = "service.impl";

    /**
     * Controller 包名
     */
    private String controllerPackage = "controller";

    // ==================== 路径配置 ====================

    /**
     * Java 源码根路径
     */
    private String javaPath = "src/main/java";

    /**
     * 资源文件根路径
     */
    private String resourcesPath = "src/main/resources";

    /**
     * Mapper XML 输出路径
     */
    private String mapperXmlPath = "src/main/resources/mapper";

    // ==================== 表配置 ====================

    /**
     * 需要生成的表名（为空则生成所有表）
     */
    private String[] tableNames = {};

    /**
     * 表名前缀（生成时会移除）
     */
    private String[] tablePrefixes = {"sys_", "biz_"};

    // ==================== 模板配置 ====================

    /**
     * 是否覆盖已有文件
     */
    private boolean fileOverride = false;

    /**
     * 是否生成 Swagger 注解
     */
    private boolean swagger = true;

    /**
     * 是否使用 Lombok
     */
    private boolean lombok = true;

    /**
     * Entity 是否继承 BaseEntity
     */
    private boolean baseEntity = true;

    /**
     * 作者名
     */
    private String author = "sloth-boot";

    /**
     * 输出目录（默认当前项目）
     */
    private String outputDir = System.getProperty("user.dir");
}
