package com.sloth.boot.example.application.model.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sloth.boot.common.security.desensitize.Desensitize;
import com.sloth.boot.common.security.desensitize.DesensitizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户视图对象。
 * <p>
 * 用于接口响应的用户信息，包含脱敏处理。
 * 手机号、身份证号、邮箱自动脱敏返回。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "用户信息")
public class SysUserVO {

    /** 主键ID */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID（字符串，避免精度丢失）", example = "1")
    private Long id;

    /** 部门ID */
    @Schema(description = "部门ID", example = "100")
    private Long deptId;

    /** 用户名 */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /** 手机号（脱敏：138****8000） */
    @Desensitize(type = DesensitizeType.MOBILE)
    @Schema(description = "手机号（脱敏：138****8000）", example = "13800138000")
    private String phone;

    /** 身份证号（脱敏：110101********1234） */
    @Desensitize(type = DesensitizeType.ID_CARD)
    @Schema(description = "身份证号（脱敏：110101********1234）", example = "110101199001011234")
    private String idCard;

    /** 邮箱（脱敏：s***@example.com） */
    @Desensitize(type = DesensitizeType.EMAIL)
    @Schema(description = "邮箱（脱敏：s***@example.com）", example = "sloth@example.com")
    private String email;

    /** 性别（0-未知, 1-男, 2-女） */
    @Schema(description = "性别", example = "1")
    private Integer gender;

    /** 状态（0-正常, 1-停用） */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 扩展信息：存入DB时自动序列化为JSON，读取时自动反序列化为Map。 */
    @Schema(description = "扩展信息（JSON存储）")
    private Map<String, Object> extraInfo;

    /** 创建人 */
    @Schema(description = "创建人", example = "admin")
    private String createBy;

    /** 创建时间 */
    @Schema(description = "创建时间", example = "2026-06-12 10:00:00")
    private LocalDateTime createTime;

    /** 更新人 */
    @Schema(description = "更新人", example = "admin")
    private String updateBy;

    /** 更新时间 */
    @Schema(description = "更新时间", example = "2026-06-12 10:00:00")
    private LocalDateTime updateTime;
}
