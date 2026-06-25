package com.sloth.boot.example.adapter.controller.user;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.model.vo.user.SysUserVO;
import com.sloth.boot.example.application.query.user.UserQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户详情查询接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class QueryUserController {

    private final UserQuery userDetailQuery;

    @Operation(summary = "查询用户详情")
    @Parameter(name = "id", description = "用户ID", required = true, example = "1")
    @GetMapping("/{id}")
    public R<SysUserVO> queryUser(@PathVariable Long id) {
        return R.ok(userDetailQuery.execute(id));
    }
}
