package com.sloth.boot.example.adapter.controller.dept;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.model.vo.dept.DeptVO;
import com.sloth.boot.example.application.query.dept.DeptQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部门详情查询接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class GetDeptController {

    private final DeptQuery deptDetailQuery;

    @Operation(summary = "查询部门详情")
    @Parameter(name = "id", description = "部门ID", required = true, example = "1")
    @GetMapping("/{id}")
    public R<DeptVO> get(@PathVariable Long id) {
        return R.ok(deptDetailQuery.execute(id));
    }
}
