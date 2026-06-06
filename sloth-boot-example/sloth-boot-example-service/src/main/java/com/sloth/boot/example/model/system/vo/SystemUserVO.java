package com.sloth.boot.example.model.system.vo;

import com.sloth.boot.common.security.desensitize.Desensitize;
import com.sloth.boot.common.security.desensitize.DesensitizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * 系统用户视图对象
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "系统用户视图对象（含数据脱敏）")
public class SystemUserVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Desensitize(type = DesensitizeType.MOBILE)
    @Schema(description = "手机号（脱敏：138****8000）", example = "13800138000")
    private String phone;

    @Desensitize(type = DesensitizeType.ID_CARD)
    @Schema(description = "身份证号（脱敏：110101********1234）", example = "110101199001011234")
    private String idCard;

    @Desensitize(type = DesensitizeType.EMAIL)
    @Schema(description = "邮箱（脱敏：d***r@sloth.boot）", example = "admin@sloth.boot")
    private String email;

    @Schema(description = "角色集合", example = "[\"admin\", \"user\"]")
    private Set<String> roles;
}
