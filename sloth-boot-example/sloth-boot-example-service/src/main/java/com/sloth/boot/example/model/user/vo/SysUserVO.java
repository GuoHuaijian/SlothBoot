package com.sloth.boot.example.model.user.vo;

import com.sloth.boot.common.security.desensitize.Desensitize;
import com.sloth.boot.common.security.desensitize.DesensitizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户视图对象（含数据脱敏）
 * <p>
 * 演示 {@link Desensitize} 注解：返回给前端时自动对敏感字段进行脱敏处理。
 * <ul>
 *   <li>手机号：138****8000</li>
 *   <li>身份证号：110101********1234</li>
 *   <li>邮箱：z***n@sloth.boot</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "用户视图对象（敏感字段自动脱敏）")
public class SysUserVO {

    /** 用户ID */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /** 所属部门ID */
    @Schema(description = "所属部门ID", example = "2")
    private Long deptId;

    /** 用户名 */
    @Schema(description = "用户名", example = "dev_user")
    private String username;

    /** 手机号（脱敏后展示） */
    @Desensitize(type = DesensitizeType.MOBILE)
    @Schema(description = "手机号（脱敏：138****8000）", example = "13800138000")
    private String phone;

    /** 身份证号（脱敏后展示） */
    @Desensitize(type = DesensitizeType.ID_CARD)
    @Schema(description = "身份证号（脱敏：110101********1234）", example = "110101199001011234")
    private String idCard;

    /** 邮箱（脱敏后展示） */
    @Desensitize(type = DesensitizeType.EMAIL)
    @Schema(description = "邮箱（脱敏：d***r@sloth.boot）", example = "dev@sloth.boot")
    private String email;

    /** 性别 */
    @Schema(description = "性别（0-未知, 1-男, 2-女）", example = "1")
    private Integer gender;

    /** 状态 */
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /** 扩展信息 */
    @Schema(description = "扩展信息（JSON自动反序列化）")
    private Map<String, Object> extraInfo;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新人 */
    @Schema(description = "更新人")
    private String updateBy;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
