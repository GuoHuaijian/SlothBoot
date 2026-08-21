package com.sloth.boot.generator.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.sloth.boot.generator.config.GeneratorConfig;
import com.sloth.boot.generator.output.WriteStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * 基于 H2 内存库的端到端生成测试：验证元数据读取、模板渲染与文件落盘全链路。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
class CodeGeneratorEndToEndTest {

    private static final String URL = "jdbc:h2:mem:generator_e2e;DB_CLOSE_DELAY=-1";

    @TempDir
    Path tempDir;

    private GeneratorConfig config;

    @BeforeEach
    void setUp() throws Exception {
        try (var connection = java.sql.DriverManager.getConnection(URL);
             var statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS \"sys_user\"");
            statement.execute("""
                CREATE TABLE "sys_user" (
                    "id" BIGINT NOT NULL,
                    "username" VARCHAR(64) NOT NULL,
                    "nickname" VARCHAR(64),
                    "status" TINYINT NOT NULL DEFAULT 1,
                    "balance" DECIMAL(10, 2),
                    "birthday" DATE,
                    "create_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    "update_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    "deleted" TINYINT NOT NULL DEFAULT 0,
                    "version" INT NOT NULL DEFAULT 1,
                    PRIMARY KEY ("id")
                )""");
            statement.execute("CREATE UNIQUE INDEX \"uk_username\" ON \"sys_user\" (\"username\")");
            statement.execute("COMMENT ON TABLE \"sys_user\" IS '系统用户表'");
            statement.execute("COMMENT ON COLUMN \"sys_user\".\"username\" IS '用户名'");
        }

        config = new GeneratorConfig();
        config.setUrl(URL);
        config.setRootPackage("com.example.app");
        config.setModuleName("user");
        config.setOutputDir(tempDir.toString());
        config.setTableNames(List.of("sys_user"));
    }

    @Test
    void generateShouldProduceFullColaStructure() throws Exception {
        GenerationResult result = new CodeGenerator(config).generate();

        assertEquals(14, result.getWrittenCount());
        assertEquals(0, result.getSkippedCount());

        String base = tempDir.resolve("src/main/java/com/example/app").toString();
        assertFileExists(base + "/adapter/controller/user/UserController.java");
        assertFileExists(base + "/application/command/user/UserSaveCommand.java");
        assertFileExists(base + "/application/command/user/UserUpdateCommand.java");
        assertFileExists(base + "/application/command/user/UserDeleteCommand.java");
        assertFileExists(base + "/application/query/user/UserGetQuery.java");
        assertFileExists(base + "/application/query/user/UserPageQuery.java");
        assertFileExists(base + "/application/model/form/user/UserForm.java");
        assertFileExists(base + "/application/model/query/user/UserQry.java");
        assertFileExists(base + "/application/model/vo/user/UserVO.java");
        assertFileExists(base + "/application/model/convert/user/UserConvert.java");
        assertFileExists(base + "/application/model/enums/user/UserErrorCode.java");
        assertFileExists(base + "/infrastructure/model/po/user/User.java");
        assertFileExists(base + "/infrastructure/repository/mapper/user/UserMapper.java");
        // XML 与接口同包（Sloth Boot 约定）
        assertFileExists(base + "/infrastructure/repository/mapper/user/UserMapper.xml");
    }

    @Test
    void generatedCodeShouldFollowProjectConventions() throws Exception {
        new CodeGenerator(config).generate();
        String base = tempDir.resolve("src/main/java/com/example/app").toString();

        String po = Files.readString(Path.of(base + "/infrastructure/model/po/user/User.java"));
        assertTrue(po.contains("@TableName(\"sys_user\")"));
        assertTrue(po.contains("public class User extends BaseEntity"));
        assertTrue(po.contains("private String username;"), "PO 应只含业务列，审计列由基类提供");

        String controller = Files.readString(Path.of(base + "/adapter/controller/user/UserController.java"));
        assertTrue(controller.contains("@RequestMapping(\"/api/users\")"));
        assertTrue(controller.contains("return R.ok(userSaveCommand.execute(form));"));
        assertTrue(controller.contains("@OperateLog(module = \"系统用户管理\", description = \"新增系统用户\", type = OperateTypeEnum.CREATE)"));

        String pageQuery = Files.readString(Path.of(base + "/application/query/user/UserPageQuery.java"));
        assertTrue(pageQuery.contains(".likeIfPresent(User::getUsername, query.username)"));
        assertTrue(pageQuery.contains(".eqIfPresent(User::getStatus, query.status)"));
        assertTrue(pageQuery.contains("PageResult.of(voList,"));

        String errorCode = Files.readString(Path.of(base + "/application/model/enums/user/UserErrorCode.java"));
        assertTrue(errorCode.contains("NOT_FOUND(100101, \"系统用户不存在\")"));
        assertTrue(errorCode.contains("ALREADY_EXISTS(100102, \"系统用户已存在\")"));

        String mapperXml = Files.readString(
            Path.of(base + "/infrastructure/repository/mapper/user/UserMapper.xml"));
        assertTrue(mapperXml.contains("<id column=\"id\" property=\"id\"/>"));
        assertTrue(mapperXml.contains("id, username, nickname, status, balance, birthday,"
            + " create_time, update_time, deleted, version"));
    }

    @Test
    void regenerateWithoutOverrideShouldSkipAllFiles() {
        new CodeGenerator(config).generate();
        config.setTableNames(List.of());
        config.setExcludeTables(List.of());

        GenerationResult secondRun = new CodeGenerator(config).generate();

        assertEquals(14, secondRun.getWrittenCount() + secondRun.getSkippedCount());
        assertEquals(14, secondRun.getSkippedCount(), "未开启覆盖时应全部跳过");
    }

    @Test
    void fileOverrideShouldRewriteExistingFiles() throws Exception {
        new CodeGenerator(config).generate();
        config.setFileOverride(true);

        GenerationResult secondRun = new CodeGenerator(config).generate();

        assertEquals(14, secondRun.getWrittenCount(), "开启覆盖后应全部重写");
        assertEquals(0, secondRun.getSkippedCount());
    }

    private void assertFileExists(String path) {
        assertTrue(Files.exists(Path.of(path)), "文件应存在: " + path);
    }
}
