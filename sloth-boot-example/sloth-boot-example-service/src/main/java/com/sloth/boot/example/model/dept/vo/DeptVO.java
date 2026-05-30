package com.sloth.boot.example.model.dept.vo;

import com.sloth.boot.common.base.TreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 部门视图对象
 * <p>
 * 继承 {@link TreeNode}，支持通过 {@link com.sloth.boot.common.util.TreeUtil#buildTree} 构建部门树。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门视图对象（支持树结构）")
public class DeptVO extends TreeNode {

    // id, parentId, sort 均继承自 TreeNode

    /** 部门名称 */
    @Schema(description = "部门名称", example = "技术研发部")
    private String name;

    /** 负责人 */
    @Schema(description = "负责人", example = "张三")
    private String leader;

    /** 状态 */
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /** 祖级列表 */
    @Schema(description = "祖级列表", example = "0,1")
    private String ancestors;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // children 字段继承自 TreeNode（List<TreeNode>），TreeUtil.buildTree 会正确填充 DeptVO 实例
}
