package com.sloth.boot.example.application.command.dept;

import com.sloth.boot.example.application.helper.dept.DeptAssembler;
import com.sloth.boot.example.application.model.form.dept.DeptCreateForm;
import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import com.sloth.boot.example.infrastructure.repository.mapper.dept.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 创建部门命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreateDeptCommand {

    private final SysDeptMapper sysDeptMapper;
    private final DeptAssembler deptAssembler;

    /**
     * 执行创建部门操作。
     * <p>
     * 将部门表单数据转换为实体并持久化到数据库。
     * 如果未指定父部门ID，则默认为顶级部门（parentId=0）。
     *
     * @param form 部门创建表单
     * @return 创建成功的部门ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long execute(DeptCreateForm form) {
        SysDept dept = deptAssembler.assembleDept(form);
        sysDeptMapper.insert(dept);
        log.info("创建部门成功: id={}, name={}", dept.getId(), dept.getName());
        return dept.getId();
    }
}
