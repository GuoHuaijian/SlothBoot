package com.sloth.boot.example.adapter.controller.dept;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.dept.BatchImportDeptsCommand;
import com.sloth.boot.example.application.model.form.dept.DeptCreateForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 批量导入部门接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class BatchImportDeptsController {

    private final BatchImportDeptsCommand batchImportDeptsCommand;

    @Operation(summary = "批量导入部门")
    @PostMapping("/import")
    public R<Integer> importDepts(@Valid @RequestBody List<DeptCreateForm> depts) {
        return R.ok(batchImportDeptsCommand.execute(depts));
    }
}
