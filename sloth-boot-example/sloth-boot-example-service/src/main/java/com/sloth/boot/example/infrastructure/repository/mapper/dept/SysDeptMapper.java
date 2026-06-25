package com.sloth.boot.example.infrastructure.repository.mapper.dept;

import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import com.sloth.boot.starter.mybatis.annotation.DataScope;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门数据访问层。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Mapper
public interface SysDeptMapper extends BaseMapperX<SysDept> {

    void insertBatch(@Param("list") List<SysDept> depts);

    List<SysDept> listDept();

    @DataScope(deptAlias = "d")
    List<SysDept> listDeptWithScope();
}
