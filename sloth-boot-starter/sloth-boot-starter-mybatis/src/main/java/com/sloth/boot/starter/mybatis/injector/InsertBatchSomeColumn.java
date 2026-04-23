package com.sloth.boot.starter.mybatis.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * 批量插入方法注入器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class InsertBatchSomeColumn extends AbstractMethod {

    /**
     * 构造函数。
     */
    public InsertBatchSomeColumn() {
        super("insertBatch");
    }

    /**
     * 注入批量插入语句。
     *
     * @param mapperClass Mapper 类型
     * @param modelClass  实体类型
     * @param tableInfo   表信息
     * @return MappedStatement
     */
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String fieldSql = prepareFieldSql(tableInfo);
        String valueSql = prepareValuesSql(tableInfo);
        String sql = "<script>INSERT INTO " + tableInfo.getTableName()
                + " " + fieldSql
                + " VALUES "
                + "<foreach collection=\"list\" item=\"item\" separator=\",\">" + valueSql + "</foreach>"
                + "</script>";
        return this.addInsertMappedStatement(mapperClass, modelClass, this.methodName,
                this.createSqlSource(this.configuration, sql, modelClass), null, tableInfo);
    }

    private String prepareFieldSql(TableInfo tableInfo) {
        StringBuilder columns = new StringBuilder("(");
        if (tableInfo.havePK()) {
            columns.append(tableInfo.getKeyColumn()).append(',');
        }
        tableInfo.getFieldList().forEach(field -> columns.append(field.getColumn()).append(','));
        columns.deleteCharAt(columns.length() - 1).append(')');
        return columns.toString();
    }

    private String prepareValuesSql(TableInfo tableInfo) {
        StringBuilder values = new StringBuilder("(");
        if (tableInfo.havePK()) {
            values.append("#{item.").append(tableInfo.getKeyProperty()).append("},");
        }
        tableInfo.getFieldList().forEach(field -> values.append("#{item.").append(field.getProperty()).append("},"));
        values.deleteCharAt(values.length() - 1).append(')');
        return values.toString();
    }
}
