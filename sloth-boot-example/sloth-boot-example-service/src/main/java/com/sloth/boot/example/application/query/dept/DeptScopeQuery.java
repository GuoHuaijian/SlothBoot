package com.sloth.boot.example.application.query.dept;

import com.sloth.boot.example.application.model.convert.dept.DeptConvert;
import com.sloth.boot.example.application.model.vo.dept.DeptVO;
import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import com.sloth.boot.example.infrastructure.repository.mapper.dept.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 部门数据权限查询。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class DeptScopeQuery {

    private final SysDeptMapper sysDeptMapper;
    private final DeptConvert deptConvert;

    /**
     * 执行部门数据权限查询。
     * <p>
     * 根据当前用户的数据权限范围查询部门列表。
     * 不同角色可查看不同范围的数据（全部、本部门、仅本人）。
     *
     * @return 部门列表
     */
    public List<DeptVO> execute() {
        List<SysDept> depts = sysDeptMapper.listDeptWithScope();
        return deptConvert.toVOList(depts);
    }
}
