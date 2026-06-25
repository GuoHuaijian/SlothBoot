package com.sloth.boot.example.application.query.dept;

import com.sloth.boot.common.util.TreeUtil;
import com.sloth.boot.example.application.model.convert.dept.DeptConvert;
import com.sloth.boot.example.application.model.vo.dept.DeptVO;
import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import com.sloth.boot.example.infrastructure.repository.mapper.dept.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 部门树查询。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class DeptTreeQuery {

    private final SysDeptMapper sysDeptMapper;
    private final DeptConvert deptConvert;

    /**
     * 执行部门树查询。
     * <p>
     * 查询所有部门并构建树形结构。
     *
     * @return 部门树形列表
     */
    public List<DeptVO> execute() {
        List<SysDept> depts = sysDeptMapper.listDept();
        List<DeptVO> voList = deptConvert.toVOList(depts);
        return TreeUtil.buildTree(voList, 0L);
    }
}
