package com.sloth.boot.example.model.dept.request;

import com.sloth.boot.starter.web.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 部门创建/更新请求
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "部门创建请求")
public class DeptCreateRequest {

    /** 部门名称 */
    @NotBlank(message = "部门名称不能为空")
    @Schema(description = "部门名称", example = "后端开发组", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 父部门ID */
    @Schema(description = "父部门ID，不传或为0表示顶级", example = "2")
    private Long parentId;

    /** 负责人 */
    @Schema(description = "负责人", example = "王五")
    private String leader;

    /** 显示排序 */
    @Schema(description = "显示排序", example = "1")
    private Integer sort;

    /** 状态（0-正常, 1-停用） */
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;
}
