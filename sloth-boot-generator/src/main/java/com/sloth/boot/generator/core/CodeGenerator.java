package com.sloth.boot.generator.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import com.sloth.boot.generator.config.GeneratorConfig;

/**
 * Sloth Boot 代码生成器。
 * <p>
 * 基于 MyBatis-Plus Generator 封装，支持一键生成 Entity / Mapper / Service / Controller 全套 CRUD 代码。
 * <p>
 * 使用方式：
 * <pre>{@code
 * GeneratorConfig config = new GeneratorConfig();
 * config.setUrl("jdbc:mysql://localhost:3306/mydb");
 * config.setTableNames(new String[]{"sys_user", "sys_role"});
 * config.setOutputDir("/path/to/project");
 *
 * new CodeGenerator(config).generate();
 * }</pre>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class CodeGenerator {

    private final GeneratorConfig config;

    public CodeGenerator(GeneratorConfig config) {
        this.config = config;
    }

    /**
     * 执行代码生成。
     */
    public void generate() {
        String basePackage = config.getParentPackage() + "." + config.getModuleName();

        List<String> tables = config.getTableNames().length > 0
            ? Arrays.asList(config.getTableNames()) : List.of();

        Map<OutputFile, String> pathMap = new HashMap<>();
        pathMap.put(OutputFile.entity, config.getOutputDir() + "/" + config.getJavaPath());
        pathMap.put(OutputFile.mapper, config.getOutputDir() + "/" + config.getJavaPath());
        pathMap.put(OutputFile.service, config.getOutputDir() + "/" + config.getJavaPath());
        pathMap.put(OutputFile.serviceImpl, config.getOutputDir() + "/" + config.getJavaPath());
        pathMap.put(OutputFile.controller, config.getOutputDir() + "/" + config.getJavaPath());
        pathMap.put(OutputFile.xml, config.getOutputDir() + "/" + config.getMapperXmlPath());

        FastAutoGenerator.create(config.getUrl(), config.getUsername(), config.getPassword())
            .globalConfig(builder -> builder
                .author(config.getAuthor())
                .outputDir(config.getOutputDir() + "/" + config.getJavaPath())
                .disableOpenDir()
            )
            .packageConfig(builder -> builder
                .parent(basePackage)
                .entity(config.getEntityPackage())
                .mapper(config.getMapperPackage())
                .service(config.getServicePackage())
                .serviceImpl(config.getServiceImplPackage())
                .controller(config.getControllerPackage())
                .pathInfo(pathMap)
            )
            .strategyConfig(builder -> {
                builder.addInclude(tables);

                // Entity 策略
                builder.entityBuilder()
                    .enableLombok()
                    .enableTableFieldAnnotation()
                    .enableRemoveIsPrefix()
                    .logicDeleteColumnName("deleted")
                    .versionColumnName("version");

                // 表前缀（移除表名前缀生成更简洁的类名）
                if (config.getTablePrefixes() != null && config.getTablePrefixes().length > 0) {
                    builder.addTablePrefix(config.getTablePrefixes());
                }

                // Mapper 策略
                builder.mapperBuilder()
                    .enableMapperAnnotation();

                // Service 策略
                builder.serviceBuilder()
                    .formatServiceFileName("%sService");

                // Controller 策略
                builder.controllerBuilder()
                    .enableRestStyle()
                    .enableHyphenStyle();
            })
            .templateEngine(new VelocityTemplateEngine())
            .execute();
    }

    /**
     * 快速生成（使用默认配置）。
     *
     * @param url       数据库 URL
     * @param username  用户名
     * @param password  密码
     * @param tables    表名
     * @param outputDir 输出目录
     */
    public static void quickGenerate(String url, String username, String password,
                                     String[] tables, String outputDir) {
        GeneratorConfig config = new GeneratorConfig();
        config.setUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setTableNames(tables);
        config.setOutputDir(outputDir);
        new CodeGenerator(config).generate();
    }

    /**
     * 命令行入口。
     *
     * @param args 参数：url username password outputDir table1 [table2] ...
     */
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("用法: java -jar sloth-boot-generator.jar <url> <username> <password> <outputDir> <table1> [table2] ...");
            System.out.println("示例: java -jar sloth-boot-generator.jar jdbc:mysql://localhost:3306/mydb root root ./output sys_user sys_role");
            return;
        }
        String url = args[0];
        String username = args[1];
        String password = args[2];
        String outputDir = args[3];
        String[] tables = new String[args.length - 4];
        System.arraycopy(args, 4, tables, 0, tables.length);

        quickGenerate(url, username, password, tables, outputDir);
        System.out.println("代码生成完成！输出目录: " + outputDir);
    }
}
