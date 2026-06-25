package com.sloth.boot.example.infrastructure.model.po.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.common.security.desensitize.Desensitize;
import com.sloth.boot.common.security.desensitize.DesensitizeType;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import com.sloth.boot.starter.mybatis.handler.EncryptTypeHandler;
import com.sloth.boot.starter.mybatis.handler.JsonTypeHandler;
import com.sloth.boot.starter.web.validator.IdCard;
import com.sloth.boot.starter.web.validator.Phone;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

import java.util.Map;

/**
 * 用户实体。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user", autoResultMap = true)
public class SysUser extends BaseEntity {

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号：写入时AES加密，读取时自动解密。
     */
    @Phone
    @Desensitize(type = DesensitizeType.MOBILE)
    @TableField(typeHandler = EncryptTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private String phone;

    /**
     * 身份证号：写入时AES加密，读取时自动解密。
     */
    @IdCard
    @Desensitize(type = DesensitizeType.ID_CARD)
    @TableField(typeHandler = EncryptTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private String idCard;

    @Desensitize(type = DesensitizeType.EMAIL)
    private String email;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 扩展信息：存入DB时自动序列化为JSON，读取时自动反序列化为Map。
     */
    @TableField(typeHandler = JsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private Map<String, Object> extraInfo;
}
