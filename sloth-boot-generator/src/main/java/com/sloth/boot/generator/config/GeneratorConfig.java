package com.sloth.boot.generator.config;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

/**
 * 代码生成器配置。
 * <p>
 * 覆盖数据源连接、输出路径、包结构与分层开关；所有生成物均可通过
 * {@code generateXxx} 开关独立启停，基础类引用可通过 FQCN 配置替换。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class GeneratorConfig {

    // ==================== 数据源配置 ====================

    /**
     * JDBC URL（MySQL 建议追加 useInformationSchema=true 以读取列注释）
     */
    private String url = "";

    /**
     * 数据库用户名
     */
    private String username = "";

    /**
     * 数据库密码
     */
    private String password = "";

    // ==================== 输出配置 ====================

    /**
     * 输出根目录（默认当前工作目录）
     */
    private String outputDir = System.getProperty("user.dir");

    /**
     * Java 源码根路径（相对 outputDir）
     */
    private String javaSourcePath = "src/main/java";

    /**
     * Mapper XML 是否与接口同包输出（Sloth Boot 约定，需配合 resources 打包规则）
     */
    private boolean mapperXmlSamePackage = true;

    /**
     * Mapper XML 独立输出路径（mapperXmlSamePackage=false 时生效，相对 outputDir）
     */
    private String mapperXmlPath = "src/main/resources/mapper";

    /**
     * 已存在文件是否覆盖
     */
    private boolean fileOverride = false;

    // ==================== 包结构配置 ====================

    /**
     * 业务模块根包
     */
    private String rootPackage = "com.example.app";

    /**
     * 模块名（作为各分层包的最后一级，如 user）
     */
    private String moduleName = "system";

    /**
     * Controller 包（相对 rootPackage）
     */
    private String controllerPackage = "adapter.controller";

    /**
     * Command 包（相对 rootPackage）
     */
    private String commandPackage = "application.command";

    /**
     * Query 包（相对 rootPackage）
     */
    private String queryPackage = "application.query";

    /**
     * 表单对象包（相对 rootPackage）
     */
    private String formPackage = "application.model.form";

    /**
     * 查询参数包（相对 rootPackage）
     */
    private String queryModelPackage = "application.model.query";

    /**
     * VO 包（相对 rootPackage）
     */
    private String voPackage = "application.model.vo";

    /**
     * MapStruct 转换器包（相对 rootPackage）
     */
    private String convertPackage = "application.model.convert";

    /**
     * 错误码枚举包（相对 rootPackage）
     */
    private String errorCodePackage = "application.model.enums";

    /**
     * PO 包（相对 rootPackage）
     */
    private String poPackage = "infrastructure.model.po";

    /**
     * Mapper 包（相对 rootPackage）
     */
    private String mapperPackage = "infrastructure.repository.mapper";

    // ==================== 表选择配置 ====================

    /**
     * 需要生成的表名（为空则生成数据库全部表）
     */
    private List<String> tableNames = List.of();

    /**
     * 排除的表名
     */
    private List<String> excludeTables = List.of();

    /**
     * 移除的表名前缀
     */
    private List<String> tablePrefixes = Arrays.asList("sys_", "biz_");

    // ==================== 基础类引用 ====================

    /**
     * 实体基类 FQCN（空表示不继承）
     */
    private String baseEntityFqcn = "com.sloth.boot.starter.mybatis.core.BaseEntity";

    /**
     * Mapper 基础接口 FQCN（空表示继承原生 BaseMapper）
     */
    private String baseMapperFqcn = "com.sloth.boot.starter.mybatis.core.BaseMapperX";

    /**
     * 条件构造器 FQCN
     */
    private String queryWrapperFqcn = "com.sloth.boot.starter.mybatis.core.LambdaQueryWrapperX";

    /**
     * 统一返回体 FQCN
     */
    private String resultFqcn = "com.sloth.boot.common.result.R";

    /**
     * 分页返回体 FQCN
     */
    private String pageResultFqcn = "com.sloth.boot.common.result.PageResult";

    /**
     * 分页查询参数基类 FQCN
     */
    private String baseQueryFqcn = "com.sloth.boot.common.base.BaseQuery";

    /**
     * 操作日志注解 FQCN（空表示不生成 @OperateLog）
     */
    private String operateLogFqcn = "com.sloth.boot.common.log.annotation.OperateLog";

    /**
     * 操作日志类型枚举 FQCN
     */
    private String operateTypeFqcn = "com.sloth.boot.common.log.annotation.OperateTypeEnum";

    // ==================== 功能开关 ====================

    /**
     * 是否生成 Controller
     */
    private boolean generateController = true;

    /**
     * 是否生成写操作 Command（Save/Update/Delete）
     */
    private boolean generateCommand = true;

    /**
     * 是否生成读操作 Query（Get/Page）
     */
    private boolean generateQuery = true;

    /**
     * 是否生成 Form 表单对象
     */
    private boolean generateForm = true;

    /**
     * 是否生成分页查询参数 Qry
     */
    private boolean generateQry = true;

    /**
     * 是否生成 VO 视图对象
     */
    private boolean generateVo = true;

    /**
     * 是否生成 MapStruct 转换器
     */
    private boolean generateConvert = true;

    /**
     * 是否生成错误码枚举
     */
    private boolean generateErrorCode = true;

    /**
     * 是否生成 Mapper XML
     */
    private boolean generateMapperXml = true;

    /**
     * 是否在 PO 上继承 baseEntityFqcn 指定的基类（同时排除基类已含字段）
     */
    private boolean extendsBaseEntity = true;

    /**
     * 是否生成 springdoc 注解（@Tag/@Operation/@Schema）
     */
    private boolean swaggerAnnotations = true;

    /**
     * 是否生成 jakarta 校验注解（@NotBlank/@NotNull/@Size）
     */
    private boolean validationAnnotations = true;

    // ==================== 文档与错误码 ====================

    /**
     * 作者名（写入 Javadoc）
     */
    private String author = "sloth-boot";

    /**
     * 版本号（写入 Javadoc @since）
     */
    private String sinceVersion = "1.0.0";

    /**
     * REST 接口前缀
     */
    private String apiPrefix = "/api";

    /**
     * 业务错误码前缀（如 1001 → 生成 100101、100102…）
     */
    private String errorCodePrefix = "1001";

    /**
     * 业务错误码起始序号
     */
    private int errorCodeStart = 1;
}
