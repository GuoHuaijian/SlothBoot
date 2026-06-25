package com.sloth.boot.example.application.model.form.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 登录表单。
 * <p>
 * 用于接收前端登录请求的参数，支持参数校验。
 * 示例工程使用模拟认证，实际项目中应改为真实密码验证。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "登录请求")
public class LoginForm {

    /** 用户ID（必填） */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /** 用户名（必填） */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
}
