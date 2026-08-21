package com.sloth.boot.generator.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 基于 H2 内存库的 JDBC 元数据读取器测试。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
class DatabaseMetadataReaderTest {

    private static final String URL = "jdbc:h2:mem:generator_meta;DB_CLOSE_DELAY=-1";

    private final DatabaseMetadataReader reader = new DatabaseMetadataReader();

    @BeforeEach
    void setUp() throws Exception {
        try (Connection connection = DriverManager.getConnection(URL);
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS \"sys_user\"");
            statement.execute("""
                CREATE TABLE "sys_user" (
                    "id" BIGINT NOT NULL,
                    "username" VARCHAR(64) NOT NULL,
                    "status" TINYINT NOT NULL DEFAULT 1,
                    "balance" DECIMAL(10, 2),
                    PRIMARY KEY ("id")
                )""");
            statement.execute("CREATE UNIQUE INDEX \"uk_username\" ON \"sys_user\" (\"username\")");
        }
    }

    @Test
    void readTablesShouldResolvePrimaryKeyAndUniqueColumns() {
        List<TableDefinition> tables = reader.readTables(URL, null, null, List.of("sys_user"));

        assertEquals(1, tables.size());
        TableDefinition user = tables.get(0);
        assertEquals("sys_user", user.getTableName());
        assertEquals("id", user.getPrimaryKeyColumn());
        assertTrue(user.getUniqueColumns().contains("username"));
        assertEquals(4, user.getColumns().size());
    }

    @Test
    void listTableNamesShouldContainCreatedTable() {
        List<String> tableNames = reader.listTableNames(URL, null, null);
        assertTrue(tableNames.contains("sys_user"));
    }
}
