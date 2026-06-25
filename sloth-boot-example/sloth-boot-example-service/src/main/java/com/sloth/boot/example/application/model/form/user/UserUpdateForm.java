package com.sloth.boot.example.application.model.form.user;

import com.sloth.boot.starter.web.validator.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 更新用户表单。
 * <p>
 * 用于接收前端更新用户的请求参数，支持参数校验。
 * 用户ID从路径参数获取，无需前端传递。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "更新用户请求")
public class UserUpdateForm {

    /** 用户ID（隐藏，从路径参数获取） */
    @Schema(description = "用户ID", hidden = true)
    private Long id;

    /** 手机号 */
    @Phone
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "sloth@example.com")
    private String email;

    /** 性别（0-女, 1-男） */
    @Schema(description = "性别", example = "1")
    private Integer gender;

    /** 状态（0-正常, 1-停用） */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 部门ID */
    @Schema(description = "部门ID", example = "100")
    private Long deptId;

    /** 扩展信息（JSON格式） */
    @Schema(description = "扩展信息")
    private Map<String, Object> extraInfo;
}
