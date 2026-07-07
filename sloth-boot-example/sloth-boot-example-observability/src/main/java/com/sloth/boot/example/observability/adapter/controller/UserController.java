package com.sloth.boot.example.observability.adapter.controller;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.observability.application.model.vo.UserVO;
import com.sloth.boot.example.observability.application.query.GetUserQuery;
import com.sloth.boot.example.observability.application.query.ListUsersQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户演示接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "用户演示", description = "用户列表与详情查询，演示查询延迟指标")
@RestController
@RequestMapping("/api/demo/users")
@RequiredArgsConstructor
public class UserController {

    private final ListUsersQuery listUsersQuery;
    private final GetUserQuery getUserQuery;

    @Operation(summary = "用户列表", description = "查询全部用户并记录列表查询延迟指标")
    @GetMapping
    public R<List<UserVO>> listUsers() {
        return R.ok(listUsersQuery.execute());
    }

    @Operation(summary = "用户详情", description = "查询用户详情")
    @Parameter(name = "id", description = "用户 ID", required = true, example = "1")
    @GetMapping("/{id}")
    public R<UserVO> getUser(@PathVariable Long id) {
        return R.ok(getUserQuery.execute(id));
    }
}
