package com.sloth.boot.example.application.model.vo.dept;

import com.sloth.boot.common.base.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 部门树节点。
 * <p>
 * 用于接口响应的部门树形结构信息。
 * 继承自 TreeNode，支持树形结构构建。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门树节点信息")
public class DeptVO extends TreeNode {

    /** 部门名称 */
    @Schema(description = "部门名称", example = "总裁办")
    private String name;

    /** 负责人 */
    @Schema(description = "负责人", example = "admin")
    private String leader;

    /** 状态（0-正常, 1-停用） */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 祖级列表（如：0,100,200） */
    @Schema(description = "祖级列表", example = "0,100,200")
    private String ancestors;

    /** 创建人 */
    @Schema(description = "创建人", example = "admin")
    private String createBy;

    /** 创建时间 */
    @Schema(description = "创建时间", example = "2026-06-12 10:00:00")
    private LocalDateTime createTime;
}
