package com.sloth.boot.starter.mybatis.handler;

import com.sloth.boot.common.util.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JSON 字段处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class JsonTypeHandler extends BaseTypeHandler<Object> {

    /**
     * 设置非空参数。
     *
     * @param ps        PreparedStatement
     * @param i         参数下标
     * @param parameter 参数值
     * @param jdbcType  JDBC 类型
     * @throws SQLException SQL 异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtil.toJson(parameter));
    }

    /**
     * 根据列名获取 JSON 字符串。
     *
     * @param rs         结果集
     * @param columnName 列名
     * @return JSON 字符串
     * @throws SQLException SQL 异常
     */
    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    /**
     * 根据列索引获取反序列化后的 JSON 对象。
     *
     * @param rs          结果集
     * @param columnIndex 列索引
     * @return 反序列化后的对象
     * @throws SQLException SQL 异常
     */
    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    /**
     * 从存储过程获取反序列化后的 JSON 对象。
     *
     * @param cs          CallableStatement
     * @param columnIndex 列索引
     * @return 反序列化后的对象
     * @throws SQLException SQL 异常
     */
    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    private Object parseJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JsonUtil.parseObject(json, Object.class);
    }
}
