package com.sloth.boot.generator.artifact;

import com.sloth.boot.generator.config.GeneratorConfig;
import com.sloth.boot.generator.model.TableModel;
import lombok.Getter;

import java.util.function.Predicate;

/**
 * 生成产物注册表：定义每种产物的模板位置、目标路径与启用条件。
 * <p>
 * 新增产物只需在此追加枚举项并配套模板文件，编排器无需改动。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public enum Artifact {

    /** 实体类 */
    PO("po", "templates/po.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getClassName() + ".java",
        config -> true),

    /** Mapper 接口 */
    MAPPER("mapper", "templates/mapper.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getMapperClassName() + ".java",
        config -> true),

    /** Mapper XML */
    MAPPER_XML("mapper_xml", "templates/mapper.xml.vm", PathKind.MAPPER_XML,
        model -> model.getMapperClassName() + ".xml",
        GeneratorConfig::isGenerateMapperXml),

    /** REST 控制器 */
    CONTROLLER("controller", "templates/controller.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getControllerClassName() + ".java",
        GeneratorConfig::isGenerateController),

    /** 新增 Command */
    SAVE_COMMAND("save_command", "templates/command-save.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getSaveCommandClassName() + ".java",
        GeneratorConfig::isGenerateCommand),

    /** 更新 Command */
    UPDATE_COMMAND("update_command", "templates/command-update.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getUpdateCommandClassName() + ".java",
        GeneratorConfig::isGenerateCommand),

    /** 删除 Command */
    DELETE_COMMAND("delete_command", "templates/command-delete.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getDeleteCommandClassName() + ".java",
        GeneratorConfig::isGenerateCommand),

    /** 单条查询 Query */
    GET_QUERY("get_query", "templates/query-get.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getGetQueryClassName() + ".java",
        GeneratorConfig::isGenerateQuery),

    /** 分页查询 Query */
    PAGE_QUERY("page_query", "templates/query-page.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getPageQueryClassName() + ".java",
        GeneratorConfig::isGenerateQuery),

    /** 表单对象 */
    FORM("form", "templates/form.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getFormClassName() + ".java",
        GeneratorConfig::isGenerateForm),

    /** 分页查询参数 */
    QRY("qry", "templates/qry.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getQryClassName() + ".java",
        GeneratorConfig::isGenerateQry),

    /** 视图对象 */
    VO("vo", "templates/vo.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getVoClassName() + ".java",
        GeneratorConfig::isGenerateVo),

    /** MapStruct 转换器 */
    CONVERT("convert", "templates/convert.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getConvertClassName() + ".java",
        GeneratorConfig::isGenerateConvert),

    /** 错误码枚举 */
    ERROR_CODE("error_code", "templates/error-code.java.vm", PathKind.JAVA_SOURCE,
        model -> model.getErrorCodeClassName() + ".java",
        GeneratorConfig::isGenerateErrorCode);

    /**
     * 产物标识（与 {@link TableModel#importsFor} 的 key 一致）
     */
    @Getter
    private final String id;

    /**
     * 模板类路径
     */
    @Getter
    private final String templateLocation;

    /**
     * 路径类型
     */
    private final PathKind pathKind;

    private final java.util.function.Function<TableModel, String> fileNameResolver;

    private final Predicate<GeneratorConfig> enabledResolver;

    Artifact(String id, String templateLocation, PathKind pathKind,
             java.util.function.Function<TableModel, String> fileNameResolver,
             Predicate<GeneratorConfig> enabledResolver) {
        this.id = id;
        this.templateLocation = templateLocation;
        this.pathKind = pathKind;
        this.fileNameResolver = fileNameResolver;
        this.enabledResolver = enabledResolver;
    }

    /**
     * 计算产物相对 outputDir 的完整文件路径。
     *
     * @param model  视图模型
     * @param config 生成配置
     * @return 相对路径（如 src/main/java/com/example/app/user/adapter/controller/user/UserController.java）
     */
    public String resolveRelativePath(TableModel model, GeneratorConfig config) {
        String fileName = fileNameResolver.apply(model);
        return switch (pathKind) {
            case JAVA_SOURCE -> toJavaFilePath(config.getJavaSourcePath(), packageOf(model), fileName);
            case MAPPER_XML -> config.isMapperXmlSamePackage()
                ? toJavaFilePath(config.getJavaSourcePath(), model.getMapperPackage(), fileName)
                : joinPath(config.getMapperXmlPath(), fileName);
        };
    }

    /**
     * 该产物是否在当前配置下启用。
     */
    public boolean isEnabled(GeneratorConfig config) {
        return enabledResolver.test(config);
    }

    private String packageOf(TableModel model) {
        return switch (this) {
            case PO -> model.getPoPackage();
            case MAPPER, MAPPER_XML -> model.getMapperPackage();
            case CONTROLLER -> model.getControllerPackage();
            case SAVE_COMMAND, UPDATE_COMMAND, DELETE_COMMAND -> model.getCommandPackage();
            case GET_QUERY, PAGE_QUERY -> model.getQueryPackage();
            case FORM -> model.getFormPackage();
            case QRY -> model.getQryPackage();
            case VO -> model.getVoPackage();
            case CONVERT -> model.getConvertPackage();
            case ERROR_CODE -> model.getErrorCodePackage();
        };
    }

    private String toJavaFilePath(String sourceRoot, String packageName, String fileName) {
        return joinPath(sourceRoot, packageName.replace('.', '/'), fileName);
    }

    private String joinPath(String... segments) {
        return String.join("/", segments);
    }

    /**
     * 产物输出路径类型。
     */
    private enum PathKind {
        /** Java 源码目录 */
        JAVA_SOURCE,
        /** Mapper XML 目录（同包或独立资源目录） */
        MAPPER_XML
    }
}
