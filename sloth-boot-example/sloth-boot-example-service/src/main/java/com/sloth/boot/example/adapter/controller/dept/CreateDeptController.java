package com.sloth.boot.example.adapter.controller.dept;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.dept.CreateDeptCommand;
import com.sloth.boot.example.application.model.form.dept.DeptCreateForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建部门接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class CreateDeptController {

    private final CreateDeptCommand createDeptCommand;

    @Operation(summary = "创建部门")
    @OperateLog(module = "部门管理", description = "创建部门", type = OperateTypeEnum.CREATE)
    @PostMapping
    public R<Long> create(@Valid @RequestBody DeptCreateForm form) {
        return R.ok(createDeptCommand.execute(form));
    }
}
