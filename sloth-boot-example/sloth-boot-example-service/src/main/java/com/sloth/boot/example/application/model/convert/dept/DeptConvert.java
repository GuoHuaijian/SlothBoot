package com.sloth.boot.example.application.model.convert.dept;

import com.sloth.boot.example.application.model.form.dept.DeptCreateForm;
import com.sloth.boot.example.application.model.form.dept.DeptUpdateForm;
import com.sloth.boot.example.application.model.vo.dept.DeptVO;
import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 部门对象转换器（MapStruct）。
 * <p>
 * 负责部门相关对象之间的转换，包括：
 * - 表单对象 → 实体对象
 * - 实体对象 → 视图对象
 * - 更新表单 → 实体对象（部分更新）
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeptConvert {

    /**
     * 将部门创建表单转换为实体对象。
     *
     * @param form 部门创建表单
     * @return 部门实体
     */
    SysDept toEntity(DeptCreateForm form);

    /**
     * 将部门创建表单列表转换为实体对象列表。
     *
     * @param forms 部门创建表单列表
     * @return 部门实体列表
     */
    List<SysDept> toEntityList(List<DeptCreateForm> forms);

    /**
     * 将部门实体转换为视图对象。
     *
     * @param entity 部门实体
     * @return 部门视图对象
     */
    DeptVO toVO(SysDept entity);

    /**
     * 将部门实体列表转换为视图对象列表。
     *
     * @param entities 部门实体列表
     * @return 部门视图对象列表
     */
    List<DeptVO> toVOList(List<SysDept> entities);

    /**
     * 将部门更新表单的非空字段更新到实体对象。
     *
     * @param form   部门更新表单
     * @param entity 目标部门实体
     */
    void updateEntity(DeptUpdateForm form, @MappingTarget SysDept entity);
}
