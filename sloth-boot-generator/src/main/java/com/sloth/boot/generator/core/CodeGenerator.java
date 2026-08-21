package com.sloth.boot.generator.core;

import com.sloth.boot.common.util.AssertUtil;
import com.sloth.boot.generator.artifact.Artifact;
import com.sloth.boot.generator.config.GeneratorConfig;
import com.sloth.boot.generator.config.GeneratorPropertiesLoader;
import com.sloth.boot.generator.metadata.DatabaseMetadataReader;
import com.sloth.boot.generator.metadata.TableDefinition;
import com.sloth.boot.generator.model.ModelFactory;
import com.sloth.boot.generator.model.TableModel;
import com.sloth.boot.generator.output.FileSystemOutputWriter;
import com.sloth.boot.generator.output.OutputWriter;
import com.sloth.boot.generator.output.WriteStatus;
import com.sloth.boot.generator.render.TemplateEngine;
import com.sloth.boot.generator.render.VelocityTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Sloth Boot 代码生成器。
 * <p>
 * 从数据库读取表结构，按 Sloth Boot COLA 分层约定生成全套代码：
 * PO / Mapper / Mapper XML / Controller / Command / Query / Form / Qry / VO / Convert / ErrorCode。
 * <p>
 * 使用方式：
 * <pre>{@code
 * GeneratorConfig config = new GeneratorConfig();
 * config.setUrl("jdbc:mysql://localhost:3306/sloth_boot?useInformationSchema=true");
 * config.setTableNames(List.of("sys_user"));
 *
 * GenerationResult result = new CodeGenerator(config).generate();
 * }</pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class CodeGenerator {

    private final GeneratorConfig config;
    private final DatabaseMetadataReader metadataReader;
    private final ModelFactory modelFactory;
    private final TemplateEngine templateEngine;
    private final OutputWriter outputWriter;

    public CodeGenerator(GeneratorConfig config) {
        this(config, new DatabaseMetadataReader(), new ModelFactory(),
            new VelocityTemplateEngine(), new FileSystemOutputWriter());
    }

    public CodeGenerator(GeneratorConfig config, DatabaseMetadataReader metadataReader, ModelFactory modelFactory,
                         TemplateEngine templateEngine, OutputWriter outputWriter) {
        this.config = config;
        this.metadataReader = metadataReader;
        this.modelFactory = modelFactory;
        this.templateEngine = templateEngine;
        this.outputWriter = outputWriter;
    }

    /**
     * 执行代码生成。
     *
     * @return 生成结果报告
     */
    public GenerationResult generate() {
        validate();
        List<String> tables = resolveTables();
        log.info("开始生成代码: 表={}, 模块={}, 输出目录={}", tables, config.getModuleName(), config.getOutputDir());

        List<TableDefinition> definitions = metadataReader.readTables(
            config.getUrl(), config.getUsername(), config.getPassword(), tables);

        GenerationResult result = new GenerationResult();
        for (int i = 0; i < definitions.size(); i++) {
            generateOne(definitions.get(i), i, result);
        }
        log.info("{}", result.summarize());
        return result;
    }

    /**
     * 快速生成（其余配置全部使用默认值）。
     *
     * @param url      JDBC URL
     * @param username 用户名
     * @param password 密码
     * @param tables   表名列表
     */
    public static void quickGenerate(String url, String username, String password, List<String> tables) {
        GeneratorConfig config = new GeneratorConfig();
        config.setUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setTableNames(tables);
        new CodeGenerator(config).generate();
    }

    /**
     * 命令行入口，支持两种用法：
     * <pre>
     * --config generator.properties          配置文件方式（推荐）
     * url username password table1 [table2]  位置参数方式（其余默认）
     * </pre>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 2 && "--config".equals(args[0])) {
            GeneratorConfig config = GeneratorPropertiesLoader.load(Files.newInputStream(Path.of(args[1])));
            new CodeGenerator(config).generate();
            return;
        }
        if (args.length >= 4) {
            CodeGenerator.quickGenerate(args[0], args[1], args[2], List.of(args).subList(3, args.length));
            return;
        }
        log.error("用法: {} <properties文件> 或 <url> <username> <password> <table1> [table2] ...", "--config");
    }

    private void generateOne(TableDefinition definition, int tableIndex, GenerationResult result) {
        TableModel model = modelFactory.create(definition, config, tableIndex * 2);
        Path outputRoot = Paths.get(config.getOutputDir()).toAbsolutePath().normalize();

        for (Artifact artifact : Artifact.values()) {
            if (!artifact.isEnabled(config)) {
                continue;
            }
            String content = templateEngine.render(artifact.getTemplateLocation(), model);
            String relativePath = artifact.resolveRelativePath(model, config);
            WriteStatus status = outputWriter.write(outputRoot.resolve(relativePath), content, config.isFileOverride());
            result.addFile(relativePath, status);
        }
    }

    private void validate() {
        AssertUtil.notBlank(config.getUrl(), "数据库 URL 不能为空");
        AssertUtil.notBlank(config.getRootPackage(), "rootPackage 不能为空");
        AssertUtil.notBlank(config.getModuleName(), "moduleName 不能为空");
        AssertUtil.notBlank(config.getOutputDir(), "outputDir 不能为空");
        AssertUtil.notBlank(config.getJavaSourcePath(), "javaSourcePath 不能为空");
        AssertUtil.isTrue(config.getErrorCodeStart() >= 0 && config.getErrorCodeStart() <= 99,
            "errorCodeStart 必须在 0-99 之间");
    }

    private List<String> resolveTables() {
        List<String> tables = new ArrayList<>(config.getTableNames());
        if (tables.isEmpty()) {
            tables = metadataReader.listTableNames(config.getUrl(), config.getUsername(), config.getPassword());
            log.info("未指定表名，将生成数据库全部表: {}", tables.size());
        }
        tables.removeAll(config.getExcludeTables());
        AssertUtil.notEmpty(tables, "没有可生成的表，请检查 tableNames/excludeTables 配置");
        return tables;
    }
}
