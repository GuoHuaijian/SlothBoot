package com.sloth.boot.example.adapter.controller.dept;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.dept.DeleteDeptCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 删除部门接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DeleteDeptController {

    private final DeleteDeptCommand deleteDeptCommand;

    @Operation(summary = "删除部门")
    @Parameter(name = "id", description = "部门ID", required = true, example = "1")
    @OperateLog(module = "部门管理", description = "删除部门", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        deleteDeptCommand.execute(id);
        return R.ok();
    }
}
