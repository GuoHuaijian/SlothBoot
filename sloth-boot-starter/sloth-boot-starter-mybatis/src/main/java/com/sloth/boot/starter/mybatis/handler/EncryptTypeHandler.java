package com.sloth.boot.starter.mybatis.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.AES;
import com.sloth.boot.common.util.SpringContextUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 加密字段处理器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class EncryptTypeHandler extends BaseTypeHandler<String> {

    private static final String DEFAULT_KEY = "sloth-boot-aes!";

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
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, encrypt(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return decrypt(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return decrypt(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return decrypt(cs.getString(columnIndex));
    }

    private String encrypt(String plaintext) {
        if (StrUtil.isBlank(plaintext)) {
            return plaintext;
        }
        return new AES(resolveKey()).encryptHex(plaintext);
    }

    private String decrypt(String ciphertext) {
        if (StrUtil.isBlank(ciphertext)) {
            return ciphertext;
        }
        return new AES(resolveKey()).decryptStr(ciphertext);
    }

    private byte[] resolveKey() {
        String configuredKey = SpringContextUtil.getProperty("sloth.mybatis.encrypt-key");
        String key = StrUtil.isBlank(configuredKey) ? DEFAULT_KEY : configuredKey;
        return StrUtil.fillAfter(key, '0', 16).substring(0, 16).getBytes(StandardCharsets.UTF_8);
    }
}
