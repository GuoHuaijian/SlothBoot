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

    private volatile AES aesInstance;

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

    /**
     * 根据列名获取解密后的结果。
     *
     * @param rs         结果集
     * @param columnName 列名
     * @return 解密后的字符串
     * @throws SQLException SQL 异常
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return decrypt(rs.getString(columnName));
    }

    /**
     * 根据列索引获取解密后的结果。
     *
     * @param rs          结果集
     * @param columnIndex 列索引
     * @return 解密后的字符串
     * @throws SQLException SQL 异常
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return decrypt(rs.getString(columnIndex));
    }

    /**
     * 从存储过程获取解密后的结果。
     *
     * @param cs          CallableStatement
     * @param columnIndex 列索引
     * @return 解密后的字符串
     * @throws SQLException SQL 异常
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return decrypt(cs.getString(columnIndex));
    }

    private String encrypt(String plaintext) {
        if (StrUtil.isBlank(plaintext)) {
            return plaintext;
        }
        return getAes().encryptHex(plaintext);
    }

    private String decrypt(String ciphertext) {
        if (StrUtil.isBlank(ciphertext)) {
            return ciphertext;
        }
        return getAes().decryptStr(ciphertext);
    }

    private AES getAes() {
        if (aesInstance == null) {
            synchronized (this) {
                if (aesInstance == null) {
                    aesInstance = new AES(resolveKey());
                }
            }
        }
        return aesInstance;
    }

    private byte[] resolveKey() {
        String configuredKey = SpringContextUtil.getProperty("sloth.mybatis.encrypt-key");
        if (StrUtil.isBlank(configuredKey)) {
            throw new IllegalStateException("sloth.mybatis.encrypt-key 未配置。"
                + "使用加密字段处理器必须在配置文件中设置 sloth.mybatis.encrypt-key");
        }
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(configuredKey.getBytes(StandardCharsets.UTF_8));
            return java.util.Arrays.copyOf(hash, 16);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
