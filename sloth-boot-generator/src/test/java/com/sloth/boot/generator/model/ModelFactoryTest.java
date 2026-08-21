package com.sloth.boot.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Types;
import java.util.List;

import com.sloth.boot.generator.config.GeneratorConfig;
import com.sloth.boot.generator.metadata.ColumnDefinition;
import com.sloth.boot.generator.metadata.TableDefinition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModelFactoryTest {

    private ModelFactory modelFactory;
    private GeneratorConfig config;
    private TableDefinition userTable;

    @BeforeEach
    void setUp() {
        modelFactory = new ModelFactory();
        config = new GeneratorConfig();
        config.setRootPackage("com.example.app");
        config.setModuleName("user");
        userTable = TableDefinition.builder()
            .tableName("sys_user")
            .remark("系统用户表")
            .primaryKeyColumn("id")
            .uniqueColumns(List.of("username"))
            .columns(List.of(
                column("id", Types.BIGINT, true),
                column("username", Types.VARCHAR, false),
                column("status", Types.TINYINT, false),
                column("balance", Types.DECIMAL, true),
                column("create_time", Types.TIMESTAMP, false),
                column("update_by", Types.VARCHAR, false),
                column("deleted", Types.TINYINT, false)))
            .build();
    }

    @Test
    void createShouldResolveNamesAndPaths() {
        TableModel model = modelFactory.create(userTable, config, 0);

        assertEquals("User", model.getClassName());
        assertEquals("user", model.getVariableName());
        assertEquals("/api/users", model.getApiPath());
        assertEquals("系统用户", model.getModuleDisplayName());
        assertEquals("com.example.app.adapter.controller.user", model.getControllerPackage());
        assertEquals("com.example.app.infrastructure.model.po.user", model.getPoPackage());
        assertEquals("UserController", model.getControllerClassName());
        assertEquals("UserSaveCommand", model.getSaveCommandClassName());
        assertEquals("userSaveCommand", model.getSaveCommandVariableName());
        assertEquals("id", model.getPkFieldName());
        assertEquals("Long", model.getPkType());
        assertEquals("getId", model.getPkGetterName());
    }

    @Test
    void createShouldFilterFieldsByArtifact() {
        TableModel model = modelFactory.create(userTable, config, 0);

        // PO 排除基类继承列（id/create_time/update_by/deleted）
        assertEquals(List.of("username", "status", "balance"),
            model.getPoFields().stream().map(FieldModel::getFieldName).toList());

        // Form 只保留业务字段
        assertEquals(List.of("username", "status", "balance"),
            model.getFormFields().stream().map(FieldModel::getFieldName).toList());

        // VO 保留主键 + 业务 + 时间展示列，排除 update_by/deleted
        assertEquals(List.of("id", "username", "status", "balance", "createTime"),
            model.getVoFields().stream().map(FieldModel::getFieldName).toList());
    }

    @Test
    void createShouldBuildErrorCodes() {
        TableModel model = modelFactory.create(userTable, config, 0);
        assertEquals(100101, model.getNotFoundErrorCode());
        assertEquals(100102, model.getDuplicateErrorCode());
        assertEquals("username", model.getUniqueFieldName());
    }

    @Test
    void duplicateCodeShouldBeNullWhenNoUniqueIndex() {
        TableDefinition table = TableDefinition.builder()
            .tableName("sys_user")
            .remark(null)
            .primaryKeyColumn("id")
            .uniqueColumns(List.of())
            .columns(List.of(column("id", Types.BIGINT, true)))
            .build();
        TableModel model = modelFactory.create(table, config, 4);

        assertNull(model.getDuplicateErrorCode());
        assertNull(model.getUniqueFieldName());
        assertEquals("User", model.getModuleDisplayName());
    }

    @Test
    void createShouldCollectImports() {
        TableModel model = modelFactory.create(userTable, config, 0);

        List<String> mapperImports = model.importsFor("mapper");
        assertTrue(mapperImports.contains("com.sloth.boot.starter.mybatis.core.BaseMapperX"));
        assertTrue(mapperImports.contains("com.example.app.infrastructure.model.po.user.User"));
        assertTrue(mapperImports.stream().sorted().toList().equals(mapperImports), "import 应有序");

        assertTrue(model.importsFor("controller").contains("io.swagger.v3.oas.annotations.tags.Tag"));
        assertTrue(model.importsFor("error_code").contains("com.sloth.boot.common.exception.ErrorCode"));
        assertTrue(model.importsFor("page_query").contains("java.util.List"));
    }

    @Test
    void createShouldBuildWrapperConditions() {
        TableModel model = modelFactory.create(userTable, config, 0);

        List<String> lines = model.getWrapperConditionLines();
        assertEquals(".likeIfPresent(User::getUsername, query.username)", lines.get(0));
        assertEquals(".eqIfPresent(User::getStatus, query.status)", lines.get(1));
    }

    @Test
    void disableBaseEntityShouldKeepAllColumnsInPo() {
        config.setExtendsBaseEntity(false);
        TableModel model = modelFactory.create(userTable, config, 0);

        assertFalse(model.isExtendsBaseEntity());
        assertEquals(7, model.getPoFields().size());
        assertTrue(model.importsFor("po").contains("java.io.Serializable"));
    }

    private ColumnDefinition column(String name, int jdbcType, boolean nullable) {
        return ColumnDefinition.builder()
            .columnName(name)
            .jdbcTypeCode(jdbcType)
            .typeName("TEST")
            .remark(name + "注释")
            .nullable(nullable)
            .columnSize(64)
            .decimalDigits(0)
            .primaryKey("id".equals(name))
            .autoIncrement(false)
            .build();
    }
}
