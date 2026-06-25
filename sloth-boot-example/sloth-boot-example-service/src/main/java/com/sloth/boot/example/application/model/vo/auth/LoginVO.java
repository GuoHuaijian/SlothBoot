package com.sloth.boot.example.application.model.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应。
 * <p>
 * 用于接口返回登录成功后的信息，包括Token令牌。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginVO {

    /** Token令牌（用于后续请求认证） */
    @Schema(description = "token令牌", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...")
    private String token;

    /** 用户ID */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /** 用户名 */
    @Schema(description = "用户名", example = "admin")
    private String username;
}
