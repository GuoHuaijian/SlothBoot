package com.sloth.boot.example.adapter.controller.dept;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.dept.UpdateDeptCommand;
import com.sloth.boot.example.application.model.form.dept.DeptUpdateForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 更新部门接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class UpdateDeptController {

    private final UpdateDeptCommand updateDeptCommand;

    @Operation(summary = "更新部门")
    @OperateLog(module = "部门管理", description = "更新部门", type = OperateTypeEnum.UPDATE)
    @PutMapping
    public R<Void> update(@RequestBody DeptUpdateForm form) {
        updateDeptCommand.execute(form);
        return R.ok();
    }
}
