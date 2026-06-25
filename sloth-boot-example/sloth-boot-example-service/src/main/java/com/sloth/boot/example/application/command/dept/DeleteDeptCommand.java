package com.sloth.boot.example.application.command.dept;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.model.enums.dept.DeptErrorCode;
import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import com.sloth.boot.example.infrastructure.repository.mapper.dept.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 删除部门命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteDeptCommand {

    private final SysDeptMapper sysDeptMapper;

    /**
     * 执行删除部门操作。
     * <p>
     * 根据部门ID删除部门记录（物理删除）。
     *
     * @param id 部门ID
     * @throws BizException 当部门不存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long id) {
        SysDept dept = sysDeptMapper.selectById(id);
        if (dept == null) throw BizException.of(DeptErrorCode.DEPT_NOT_FOUND);
        sysDeptMapper.deleteById(id);
        log.info("删除部门成功: id={}", id);
    }
}
