package com.sloth.boot.example.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import com.sloth.boot.starter.mybatis.handler.EncryptTypeHandler;
import com.sloth.boot.starter.mybatis.handler.JsonTypeHandler;
import com.sloth.boot.starter.web.validator.EnumValue;
import com.sloth.boot.starter.web.validator.IdCard;
import com.sloth.boot.starter.web.validator.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 用户实体
 * <p>
 * 演示 MyBatis-Plus 高级能力：
 * <ul>
 *   <li>{@link EncryptTypeHandler} — phone/idCard 字段 AES 加密存储，读取时自动解密</li>
 *   <li>{@link JsonTypeHandler} — extraInfo 以 JSON 字符串存储在 TEXT 列，读取时自动反序列化</li>
 *   <li>{@link Phone} / {@link IdCard} / {@link EnumValue} — JSR-380 自定义校验</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户实体")
public class SysUser extends BaseEntity {

    /**
     * 所属部门ID
     */
    @TableField("dept_id")
    @Schema(description = "所属部门ID", example = "2")
    private Long deptId;

    /**
     * 用户名
     */
    @TableField("username")
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 手机号（AES 加密存储）
     */
    @TableField(value = "phone", typeHandler = EncryptTypeHandler.class)
    @Phone
    @Schema(description = "手机号（数据库中AES加密存储，查询时自动解密）", example = "13800138000")
    private String phone;

    /**
     * 身份证号（AES 加密存储）
     */
    @TableField(value = "id_card", typeHandler = EncryptTypeHandler.class)
    @IdCard
    @Schema(description = "身份证号（数据库中AES加密存储，查询时自动解密）", example = "110101199001011234")
    private String idCard;

    /**
     * 邮箱
     */
    @TableField("email")
    @Schema(description = "邮箱", example = "zhangsan@sloth.boot")
    private String email;

    /**
     * 性别（0-未知, 1-男, 2-女）
     */
    @TableField("gender")
    @EnumValue(intValues = {0, 1, 2})
    @Schema(description = "性别（0-未知, 1-男, 2-女）", example = "1")
    private Integer gender;

    /**
     * 状态（0-正常, 1-停用）
     */
    @TableField("status")
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /**
     * 扩展信息（JSON 格式存储）
     * <p>
     * 数据库中以 TEXT 类型存储 JSON 字符串，读取时通过 {@link JsonTypeHandler} 自动反序列化为 Map。
     */
    @TableField(value = "extra_info", typeHandler = JsonTypeHandler.class)
    @Schema(description = "扩展信息（JSON格式存储，自动序列化/反序列化）")
    private Map<String, Object> extraInfo;
}
