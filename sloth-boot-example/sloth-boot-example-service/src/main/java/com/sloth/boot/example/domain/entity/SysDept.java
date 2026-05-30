package com.sloth.boot.example.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import com.sloth.boot.starter.web.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体
 * <p>
 * 演示 MyBatis-Plus 基础能力：雪花ID、自动填充、逻辑删除、乐观锁。
 * 配合 {@link com.sloth.boot.common.base.TreeNode} 和
 * {@link com.sloth.boot.common.util.TreeUtil} 实现部门树结构。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
@Schema(description = "部门实体")
public class SysDept extends BaseEntity {

    /**
     * 部门名称
     */
    @TableField("name")
    @Schema(description = "部门名称", example = "技术研发部", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 父部门ID（0 表示顶级部门）
     */
    @TableField("parent_id")
    @Schema(description = "父部门ID，0表示顶级", example = "1")
    private Long parentId;

    /**
     * 显示排序
     */
    @TableField("sort")
    @Schema(description = "显示排序", example = "1")
    private Integer sort;

    /**
     * 负责人
     */
    @TableField("leader")
    @Schema(description = "负责人", example = "张三")
    private String leader;

    /**
     * 状态（0-正常, 1-停用）
     */
    @TableField("status")
    @EnumValue(intValues = {0, 1})
    @Schema(description = "状态（0-正常, 1-停用）", example = "0")
    private Integer status;

    /**
     * 祖级列表（逗号分隔，如 0,1,2）
     */
    @TableField("ancestors")
    @Schema(description = "祖级列表", example = "0,1")
    private String ancestors;
}
