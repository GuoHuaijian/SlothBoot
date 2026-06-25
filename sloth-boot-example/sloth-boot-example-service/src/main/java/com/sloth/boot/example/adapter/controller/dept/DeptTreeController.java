package com.sloth.boot.example.adapter.controller.dept;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.model.vo.dept.DeptVO;
import com.sloth.boot.example.application.query.dept.DeptTreeQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门树查询接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DeptTreeController {

    private final DeptTreeQuery deptTreeQuery;

    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public R<List<DeptVO>> execute() {
        return R.ok(deptTreeQuery.execute());
    }
}
