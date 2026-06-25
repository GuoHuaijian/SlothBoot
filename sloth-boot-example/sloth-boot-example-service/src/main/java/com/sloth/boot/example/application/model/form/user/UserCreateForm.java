package com.sloth.boot.example.application.model.form.user;

import com.sloth.boot.starter.web.validator.EnumValue;
import com.sloth.boot.starter.web.validator.IdCard;
import com.sloth.boot.starter.web.validator.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 创建用户表单。
 * <p>
 * 用于接收前端创建用户的请求参数，支持参数校验。
 * 手机号和身份证号会自动进行AES加密存储。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "创建用户请求")
public class UserCreateForm {

    /** 用户名（必填） */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** 手机号（自动AES加密存储） */
    @Phone
    @Schema(description = "手机号（自动AES加密存储）", example = "13800138000")
    private String phone;

    /** 身份证号（自动AES加密存储） */
    @IdCard
    @Schema(description = "身份证号（自动AES加密存储）", example = "110101199001011234")
    private String idCard;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "sloth@example.com")
    private String email;

    /** 性别（0-未知, 1-男, 2-女） */
    @EnumValue(intValues = {0, 1, 2})
    @Schema(description = "性别（0-未知, 1-男, 2-女）", example = "1")
    private Integer gender;

    /** 状态（0-正常, 1-停用） */
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态（0-正常, 1-停用）", example = "1")
    private Integer status;

    /** 部门ID */
    @Schema(description = "部门ID", example = "100")
    private Long deptId;

    /** 扩展信息（JSON存储） */
    @Schema(description = "扩展信息（JSON存储）")
    private Map<String, Object> extraInfo;
}
