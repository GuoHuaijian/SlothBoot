package com.sloth.boot.example.application.model.vo.auth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sloth.boot.common.security.desensitize.Desensitize;
import com.sloth.boot.common.security.desensitize.DesensitizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * 系统用户VO（权限 + 脱敏）。
 * <p>
 * 用于接口响应的当前登录用户信息，包含脱敏处理。
 * 手机号、身份证号、邮箱自动脱敏返回。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "系统用户信息")
public class SystemUserVO {

    /** 用户ID */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = com.fasterxml.jackson.databind.deser.std.NumberDeserializers.LongDeserializer.class)
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /** 用户名 */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 密码（注意：实际返回时应为null） */
    @Schema(description = "密码", example = "123456")
    private String password;

    /** 手机号（脱敏：138****8000） */
    @Desensitize(type = DesensitizeType.MOBILE)
    @Schema(description = "手机号（脱敏）", example = "13800138000")
    private String phone;

    /** 身份证号（脱敏：110101********1234） */
    @Desensitize(type = DesensitizeType.ID_CARD)
    @Schema(description = "身份证号（脱敏）", example = "110101199001011234")
    private String idCard;

    /** 邮箱（脱敏：s***@example.com） */
    @Desensitize(type = DesensitizeType.EMAIL)
    @Schema(description = "邮箱（脱敏）", example = "sloth@example.com")
    private String email;

    /** 角色标识列表 */
    @Schema(description = "角色标识列表", example = "[\"admin\", \"user\"]")
    private Set<String> roles;
}
