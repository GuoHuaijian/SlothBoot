package com.sloth.boot.example.application.command.dept;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.model.convert.dept.DeptConvert;
import com.sloth.boot.example.application.model.enums.dept.DeptErrorCode;
import com.sloth.boot.example.application.model.form.dept.DeptUpdateForm;
import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import com.sloth.boot.example.infrastructure.repository.mapper.dept.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 更新部门命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateDeptCommand {

    private final SysDeptMapper sysDeptMapper;
    private final DeptConvert deptConvert;

    /**
     * 执行更新部门操作。
     * <p>
     * 根据部门ID查询现有部门，使用表单数据更新部门信息。
     *
     * @param form 部门更新表单
     * @throws BizException 当部门不存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute(DeptUpdateForm form) {
        SysDept existing = sysDeptMapper.selectById(form.getId());
        if (existing == null) {
            throw BizException.of(DeptErrorCode.DEPT_NOT_FOUND);
        }
        deptConvert.updateEntity(form, existing);
        sysDeptMapper.updateById(existing);
        log.info("更新部门成功: id={}", form.getId());
    }
}
