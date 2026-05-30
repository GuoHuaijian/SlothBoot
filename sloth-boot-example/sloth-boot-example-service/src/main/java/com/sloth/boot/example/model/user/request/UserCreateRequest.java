package com.sloth.boot.example.model.user.request;

import com.sloth.boot.starter.web.validator.EnumValue;
import com.sloth.boot.starter.web.validator.IdCard;
import com.sloth.boot.starter.web.validator.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 用户创建请求
 * <p>
 * 演示自定义校验注解：@Phone、@IdCard、@EnumValue
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "用户创建请求")
public class UserCreateRequest {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** 手机号（通过 @Phone 校验格式） */
    @Phone
    @Schema(description = "手机号（@Phone 格式校验）", example = "13800138000")
    private String phone;

    /** 身份证号（通过 @IdCard 校验格式） */
    @IdCard
    @Schema(description = "身份证号（@IdCard 格式校验）", example = "110101199001011234")
    private String idCard;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "zhangsan@sloth.boot")
    private String email;

    /** 性别（0-未知, 1-男, 2-女） */
    @EnumValue(intValues = {0, 1, 2})
    @Schema(description = "性别（0-未知, 1-男, 2-女）", example = "1")
    private Integer gender;

    /** 状态（0-正常, 1-停用） */
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /** 所属部门ID */
    @Schema(description = "所属部门ID", example = "2")
    private Long deptId;

    /** 扩展信息（JSON 格式） */
    @Schema(description = "扩展信息（JSON对象，通过JsonTypeHandler自动存储为JSON字符串）")
    private Map<String, Object> extraInfo;
}
