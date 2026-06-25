package com.sloth.boot.example.infrastructure.model.po.dept;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import com.sloth.boot.starter.web.validator.EnumValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 状态（0-正常，1-停用）
     */
    @EnumValue(intValues = {0, 1})
    private Integer status;

    /**
     * 祖级列表
     */
    private String ancestors;
}
