package com.sloth.boot.example.observability.application.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户视图对象。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "Alice")
    private String name;

    @Schema(description = "邮箱", example = "alice@sloth.boot")
    private String email;

    @Schema(description = "角色", example = "ADMIN")
    private String role;
}
