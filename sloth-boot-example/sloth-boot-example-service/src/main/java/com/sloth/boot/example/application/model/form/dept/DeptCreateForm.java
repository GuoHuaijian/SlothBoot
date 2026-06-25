package com.sloth.boot.example.application.model.form.dept;

import com.sloth.boot.starter.web.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建部门表单。
 * <p>
 * 用于接收前端创建部门的请求参数，支持参数校验。
 * 如果未指定父部门ID，则默认为顶级部门。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "创建部门请求")
public class DeptCreateForm {

    /** 部门名称（必填） */
    @NotBlank(message = "部门名称不能为空")
    @Schema(description = "部门名称", example = "研发部", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 父部门ID（不填则为顶级部门） */
    @Schema(description = "父部门ID", example = "100")
    private Long parentId;

    /** 负责人 */
    @Schema(description = "负责人", example = "admin")
    private String leader;

    /** 排序 */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /** 状态（0-正常, 1-停用） */
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态", example = "1")
    private Integer status;
}
