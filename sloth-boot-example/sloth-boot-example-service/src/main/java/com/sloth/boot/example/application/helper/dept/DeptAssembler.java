package com.sloth.boot.example.application.helper.dept;

import com.sloth.boot.example.application.model.convert.dept.DeptConvert;
import com.sloth.boot.example.application.model.form.dept.DeptCreateForm;
import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 部门对象组装器。
 * <p>
 * 负责部门相关的复杂对象组装逻辑，包括：
 * - 创建部门时的祖级路径计算
 * - 表单数据填充默认值
 * <p>
 * 与 {@link DeptConvert} 的区别：
 * - DeptConvert：单对象之间的简单映射（Form→Entity, Entity→VO）
 * - DeptAssembler：涉及业务规则的复杂组装（如根据父部门计算祖级路径）
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class DeptAssembler {

    private final DeptConvert deptConvert;

    private static final Long ROOT_PARENT_ID = 0L;
    private static final String ROOT_ANCESTORS = "0";

    /**
     * 将部门创建表单组装为完整的部门实体。
     * <p>
     * 组装逻辑：
     * 1. 通过 MapStruct 将表单转换为部门实体
     * 2. 如果未指定父部门ID，默认为顶级部门
     * 3. 如果未指定祖级列表，使用默认值
     *
     * @param form 部门创建表单
     * @return 组装完成的部门实体
     */
    public SysDept assembleDept(DeptCreateForm form) {
        SysDept dept = deptConvert.toEntity(form);
        fillDefaultValues(dept);
        return dept;
    }

    /**
     * 将部门创建表单组装为完整的部门实体，并设置祖级路径。
     * <p>
     * 当在某个父部门下创建子部门时使用，会根据父部门信息计算完整的祖级路径。
     * 例如：父部门路径为 "0,100"，新部门ID为 200，则新部门路径为 "0,100,200"。
     *
     * @param form       部门创建表单
     * @param parentDept 父部门实体（可为null，表示创建顶级部门）
     * @param newDeptId  新部门ID（插入数据库后获取）
     * @return 组装完成的部门实体
     */
    public SysDept assembleDeptWithAncestors(DeptCreateForm form, SysDept parentDept, Long newDeptId) {
        SysDept dept = assembleDept(form);
        if (parentDept != null) {
            dept.setAncestors(buildAncestorsPath(parentDept.getAncestors(), parentDept.getId()));
        }
        if (newDeptId != null) {
            dept.setId(newDeptId);
        }
        return dept;
    }

    /**
     * 构建祖级路径。
     * <p>
     * 将父部门的祖级路径与父部门ID拼接，形成完整的祖级路径。
     *
     * @param parentAncestors 父部门的祖级路径（如 "0,100"）
     * @param parentId        父部门ID
     * @return 完整的祖级路径（如 "0,100,200"）
     */
    public String buildAncestorsPath(String parentAncestors, Long parentId) {
        if (parentAncestors == null || parentAncestors.isEmpty()) {
            return ROOT_ANCESTORS + "," + parentId;
        }
        return parentAncestors + "," + parentId;
    }

    /**
     * 填充部门实体的默认值。
     *
     * @param dept 部门实体
     */
    private void fillDefaultValues(SysDept dept) {
        if (dept.getParentId() == null) {
            dept.setParentId(ROOT_PARENT_ID);
        }
        if (dept.getAncestors() == null) {
            dept.setAncestors(ROOT_ANCESTORS);
        }
    }
}
