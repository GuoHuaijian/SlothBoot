package com.sloth.boot.generator.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sloth.boot.common.util.AssertUtil;

/**
 * JDBC 元数据读取器，从数据库读取表结构定义。
 * <p>
 * 基于 {@link DatabaseMetaData} 实现，不依赖特定数据库驱动；
 * MySQL 建议在 URL 中追加 {@code useInformationSchema=true} 以获取完整列注释。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class DatabaseMetadataReader {

    private static final String TABLE_TYPE_TABLE = "TABLE";
    private static final String COLUMN_NAME = "COLUMN_NAME";
    private static final int NO_DECIMALS = 0;

    /**
     * 读取多张表的定义。
     *
     * @param url      JDBC URL
     * @param username 用户名
     * @param password 密码
     * @param tables   表名列表（不可为空）
     * @return 表定义列表（顺序与入参一致）
     */
    public List<TableDefinition> readTables(String url, String username, String password, List<String> tables) {
        AssertUtil.notBlank(url, "数据库 URL 不能为空");
        AssertUtil.notEmpty(tables, "表名列表不能为空");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            // 限定当前 schema，避免读到其他目录下的系统表（H2/PG 等）
            String schema = resolveCurrentSchema(connection);
            List<TableDefinition> definitions = new ArrayList<>(tables.size());
            for (String table : tables) {
                definitions.add(readTable(metaData, schema, table));
            }
            return definitions;
        } catch (SQLException e) {
            throw new IllegalStateException("读取数据库元数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 列出数据库中的全部表名。
     *
     * @param url      JDBC URL
     * @param username 用户名
     * @param password 密码
     * @return 表名列表
     */
    public List<String> listTableNames(String url, String username, String password) {
        AssertUtil.notBlank(url, "数据库 URL 不能为空");
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = resolveCurrentSchema(connection);
            List<String> tableNames = new ArrayList<>();
            try (ResultSet rs = metaData.getTables(null, schema, "%", new String[]{TABLE_TYPE_TABLE})) {
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }
            return tableNames;
        } catch (SQLException e) {
            throw new IllegalStateException("读取数据库表清单失败: " + e.getMessage(), e);
        }
    }

    private TableDefinition readTable(DatabaseMetaData metaData, String schema, String tableName) throws SQLException {
        String primaryKeyColumn = findPrimaryKeyColumn(metaData, schema, tableName);
        List<ColumnDefinition> columns = readColumns(metaData, schema, tableName, primaryKeyColumn);
        if (columns.isEmpty()) {
            throw new IllegalStateException("表不存在或没有任何列: " + tableName);
        }
        return TableDefinition.builder()
            .tableName(tableName)
            .remark(findTableRemark(metaData, schema, tableName))
            .columns(columns)
            .primaryKeyColumn(primaryKeyColumn)
            .uniqueColumns(findUniqueColumns(metaData, schema, tableName, primaryKeyColumn))
            .build();
    }

    /**
     * 解析连接的当前 schema；MySQL 驱动返回 null 时保持全库行为。
     */
    private String resolveCurrentSchema(Connection connection) throws SQLException {
        return connection.getSchema();
    }

    private String findPrimaryKeyColumn(DatabaseMetaData metaData, String schema,
                                        String tableName) throws SQLException {
        try (ResultSet rs = metaData.getPrimaryKeys(null, schema, tableName)) {
            if (rs.next()) {
                return rs.getString(COLUMN_NAME);
            }
        }
        return null;
    }

    private List<ColumnDefinition> readColumns(DatabaseMetaData metaData, String schema, String tableName,
                                               String primaryKeyColumn) throws SQLException {
        List<ColumnDefinition> columns = new ArrayList<>();
        try (ResultSet rs = metaData.getColumns(null, schema, tableName, "%")) {
            while (rs.next()) {
                columns.add(buildColumn(rs, primaryKeyColumn));
            }
        }
        return columns;
    }

    private ColumnDefinition buildColumn(ResultSet rs, String primaryKeyColumn) throws SQLException {
        String columnName = rs.getString(COLUMN_NAME);
        int jdbcTypeCode = rs.getInt("DATA_TYPE");
        int columnSize = readIntOrZero(rs, "COLUMN_SIZE");
        int decimalDigits = readIntOrZero(rs, "DECIMAL_DIGITS");
        boolean autoIncrement = isAutoIncrement(rs);

        return ColumnDefinition.builder()
            .columnName(columnName)
            .jdbcTypeCode(jdbcTypeCode)
            .typeName(rs.getString("TYPE_NAME"))
            .remark(trimToNull(rs.getString("REMARKS")))
            .nullable(!"NO".equals(rs.getString("IS_NULLABLE")))
            .columnSize(Math.max(columnSize, NO_DECIMALS))
            .decimalDigits(decimalDigits)
            .primaryKey(columnName.equals(primaryKeyColumn))
            .autoIncrement(autoIncrement)
            .build();
    }

    private boolean isAutoIncrement(ResultSet rs) throws SQLException {
        ResultSetMetaData rsMetaData = rs.getMetaData();
        for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
            if ("IS_AUTOINCREMENT".equalsIgnoreCase(rsMetaData.getColumnName(i))) {
                return "YES".equalsIgnoreCase(rs.getString(i));
            }
        }
        return false;
    }

    private String findTableRemark(DatabaseMetaData metaData, String schema, String tableName) throws SQLException {
        try (ResultSet rs = metaData.getTables(null, schema, tableName, new String[]{TABLE_TYPE_TABLE})) {
            if (rs.next()) {
                return trimToNull(rs.getString("REMARKS"));
            }
        }
        return null;
    }

    private List<String> findUniqueColumns(DatabaseMetaData metaData, String schema, String tableName,
                                           String primaryKeyColumn) throws SQLException {
        Set<String> uniqueColumns = new LinkedHashSet<>();
        try (ResultSet rs = metaData.getIndexInfo(null, null, tableName, true, false)) {
            while (rs.next()) {
                String columnName = rs.getString(COLUMN_NAME);
                // ORDINAL_POSITION 为 0 的行是索引统计信息，跳过；主键不参与唯一冲突判断
                if (columnName == null || rs.getInt("ORDINAL_POSITION") == NO_DECIMALS
                    || columnName.equals(primaryKeyColumn)) {
                    continue;
                }
                uniqueColumns.add(columnName);
            }
        }
        return new ArrayList<>(uniqueColumns);
    }

    private int readIntOrZero(ResultSet rs, String columnLabel) throws SQLException {
        int value = rs.getInt(columnLabel);
        return rs.wasNull() ? NO_DECIMALS : value;
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
