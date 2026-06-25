package com.sloth.boot.example.application.query.dept;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.model.convert.dept.DeptConvert;
import com.sloth.boot.example.application.model.enums.dept.DeptErrorCode;
import com.sloth.boot.example.application.model.vo.dept.DeptVO;
import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import com.sloth.boot.example.infrastructure.repository.mapper.dept.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 部门详情查询。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class DeptQuery {

    private final SysDeptMapper sysDeptMapper;
    private final DeptConvert deptConvert;

    /**
     * 执行部门详情查询。
     *
     * @param id 部门ID
     * @return 部门视图对象
     * @throws BizException 当部门不存在时抛出
     */
    public DeptVO execute(Long id) {
        SysDept dept = sysDeptMapper.selectById(id);
        if (dept == null) {
            throw BizException.of(DeptErrorCode.DEPT_NOT_FOUND);
        }
        return deptConvert.toVO(dept);
    }
}
