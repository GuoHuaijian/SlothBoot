package com.sloth.boot.generator.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.sloth.boot.common.util.AssertUtil;

/**
 * 基于 Properties 文件的配置加载器，供命令行方式使用。
 * <p>
 * 配置键与 {@link GeneratorConfig} 的属性名一致（小驼峰），支持逗号分隔的列表值：
 * <pre>
 * url=jdbc:mysql://localhost:3306/sloth_boot?useInformationSchema=true
 * username=root
 * password=root
 * rootPackage=com.example.app
 * moduleName=user
 * tableNames=sys_user,sys_role
 * generateErrorCode=false
 * </pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class GeneratorPropertiesLoader {

    private GeneratorPropertiesLoader() {
    }

    /**
     * 从 Properties 文件加载配置。
     *
     * @param inputStream 配置文件输入流
     * @return 配置对象
     * @throws IOException 文件读取失败
     */
    public static GeneratorConfig load(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        GeneratorConfig config = new GeneratorConfig();
        apply(properties, config);
        return config;
    }

    private static void apply(Properties p, GeneratorConfig c) {
        // 数据源
        c.setUrl(p.getProperty("url", c.getUrl()));
        c.setUsername(p.getProperty("username", c.getUsername()));
        c.setPassword(p.getProperty("password", c.getPassword()));

        // 输出
        c.setOutputDir(p.getProperty("outputDir", c.getOutputDir()));
        c.setJavaSourcePath(p.getProperty("javaSourcePath", c.getJavaSourcePath()));
        c.setMapperXmlSamePackage(booleanOf(p, "mapperXmlSamePackage", c.isMapperXmlSamePackage()));
        c.setMapperXmlPath(p.getProperty("mapperXmlPath", c.getMapperXmlPath()));
        c.setFileOverride(booleanOf(p, "fileOverride", c.isFileOverride()));

        // 包结构
        c.setRootPackage(p.getProperty("rootPackage", c.getRootPackage()));
        c.setModuleName(p.getProperty("moduleName", c.getModuleName()));
        c.setControllerPackage(p.getProperty("controllerPackage", c.getControllerPackage()));
        c.setCommandPackage(p.getProperty("commandPackage", c.getCommandPackage()));
        c.setQueryPackage(p.getProperty("queryPackage", c.getQueryPackage()));
        c.setFormPackage(p.getProperty("formPackage", c.getFormPackage()));
        c.setQueryModelPackage(p.getProperty("queryModelPackage", c.getQueryModelPackage()));
        c.setVoPackage(p.getProperty("voPackage", c.getVoPackage()));
        c.setConvertPackage(p.getProperty("convertPackage", c.getConvertPackage()));
        c.setErrorCodePackage(p.getProperty("errorCodePackage", c.getErrorCodePackage()));
        c.setPoPackage(p.getProperty("poPackage", c.getPoPackage()));
        c.setMapperPackage(p.getProperty("mapperPackage", c.getMapperPackage()));

        // 表选择
        if (p.containsKey("tableNames")) {
            c.setTableNames(splitList(p.getProperty("tableNames")));
        }
        if (p.containsKey("excludeTables")) {
            c.setExcludeTables(splitList(p.getProperty("excludeTables")));
        }
        if (p.containsKey("tablePrefixes")) {
            c.setTablePrefixes(splitList(p.getProperty("tablePrefixes")));
        }

        // 基础类引用（空字符串表示显式关闭该特性）
        c.setBaseEntityFqcn(nullableOf(p, "baseEntityFqcn", c.getBaseEntityFqcn()));
        c.setBaseMapperFqcn(nullableOf(p, "baseMapperFqcn", c.getBaseMapperFqcn()));
        c.setQueryWrapperFqcn(nullableOf(p, "queryWrapperFqcn", c.getQueryWrapperFqcn()));
        c.setResultFqcn(nullableOf(p, "resultFqcn", c.getResultFqcn()));
        c.setPageResultFqcn(nullableOf(p, "pageResultFqcn", c.getPageResultFqcn()));
        c.setOperateLogFqcn(nullableOf(p, "operateLogFqcn", c.getOperateLogFqcn()));
        c.setOperateTypeFqcn(nullableOf(p, "operateTypeFqcn", c.getOperateTypeFqcn()));

        // 功能开关
        c.setGenerateController(booleanOf(p, "generateController", c.isGenerateController()));
        c.setGenerateCommand(booleanOf(p, "generateCommand", c.isGenerateCommand()));
        c.setGenerateQuery(booleanOf(p, "generateQuery", c.isGenerateQuery()));
        c.setGenerateForm(booleanOf(p, "generateForm", c.isGenerateForm()));
        c.setGenerateQry(booleanOf(p, "generateQry", c.isGenerateQry()));
        c.setGenerateVo(booleanOf(p, "generateVo", c.isGenerateVo()));
        c.setGenerateConvert(booleanOf(p, "generateConvert", c.isGenerateConvert()));
        c.setGenerateErrorCode(booleanOf(p, "generateErrorCode", c.isGenerateErrorCode()));
        c.setGenerateMapperXml(booleanOf(p, "generateMapperXml", c.isGenerateMapperXml()));
        c.setExtendsBaseEntity(booleanOf(p, "extendsBaseEntity", c.isExtendsBaseEntity()));
        c.setSwaggerAnnotations(booleanOf(p, "swaggerAnnotations", c.isSwaggerAnnotations()));
        c.setValidationAnnotations(booleanOf(p, "validationAnnotations", c.isValidationAnnotations()));

        // 文档与错误码
        c.setAuthor(p.getProperty("author", c.getAuthor()));
        c.setSinceVersion(p.getProperty("sinceVersion", c.getSinceVersion()));
        c.setApiPrefix(p.getProperty("apiPrefix", c.getApiPrefix()));
        c.setErrorCodePrefix(p.getProperty("errorCodePrefix", c.getErrorCodePrefix()));
        if (p.containsKey("errorCodeStart")) {
            int start = Integer.parseInt(p.getProperty("errorCodeStart"));
            AssertUtil.isTrue(start >= 0 && start <= 99, "errorCodeStart 必须在 0-99 之间");
            c.setErrorCodeStart(start);
        }
    }

    private static boolean booleanOf(Properties p, String key, boolean defaultValue) {
        String value = p.getProperty(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.trim());
    }

    private static String nullableOf(Properties p, String key, String defaultValue) {
        String value = p.getProperty(key);
        return value == null ? defaultValue : value.trim();
    }

    private static List<String> splitList(String value) {
        return List.of(value.split(","));
    }
}
