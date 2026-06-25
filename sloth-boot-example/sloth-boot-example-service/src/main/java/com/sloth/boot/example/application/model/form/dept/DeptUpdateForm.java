package com.sloth.boot.example.application.model.form.dept;

import com.sloth.boot.starter.web.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新部门表单。
 * <p>
 * 用于接收前端更新部门的请求参数，支持参数校验。
 * 部门ID从路径参数获取，无需前端传递。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Schema(description = "更新部门请求")
public class DeptUpdateForm {

    /** 部门ID（隐藏，从路径参数获取） */
    @Schema(description = "部门ID", hidden = true)
    private Long id;

    /** 部门名称 */
    @Schema(description = "部门名称", example = "研发部")
    private String name;

    /** 父部门ID */
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
