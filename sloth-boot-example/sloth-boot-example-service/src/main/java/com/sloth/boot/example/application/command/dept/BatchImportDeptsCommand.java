package com.sloth.boot.example.application.command.dept;

import com.sloth.boot.example.application.model.convert.dept.DeptConvert;
import com.sloth.boot.example.application.model.form.dept.DeptCreateForm;
import com.sloth.boot.example.infrastructure.model.po.dept.SysDept;
import com.sloth.boot.example.infrastructure.repository.mapper.dept.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 批量导入部门命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchImportDeptsCommand {

    private final SysDeptMapper sysDeptMapper;
    private final DeptConvert deptConvert;

    /**
     * 执行批量导入部门操作。
     * <p>
     * 将部门表单列表转换为实体列表并批量插入数据库。
     * 如果未指定父部门ID，则默认为顶级部门（parentId=0）。
     *
     * @param forms 部门创建表单列表
     * @return 成功导入的部门数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int execute(List<DeptCreateForm> forms) {
        if (forms == null || forms.isEmpty()) {
            return 0;
        }
        List<SysDept> depts = deptConvert.toEntityList(forms);
        depts.forEach(dept -> {
            if (dept.getParentId() == null) dept.setParentId(0L);
            if (dept.getAncestors() == null) dept.setAncestors("0");
        });
        sysDeptMapper.insertBatch(depts);
        log.info("批量导入部门成功: count={}", depts.size());
        return depts.size();
    }
}
