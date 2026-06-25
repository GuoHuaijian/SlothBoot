package com.sloth.boot.example.adapter.controller.user;

import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.model.query.user.UserPageQry;
import com.sloth.boot.example.application.model.vo.user.SysUserVO;
import com.sloth.boot.example.application.query.user.PageUserQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户分页查询接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PageUserQueryController {

    private final PageUserQuery userPageQuery;

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public R<PageResult<SysUserVO>> pageUserQuery(UserPageQry query) {
        return R.ok(userPageQuery.execute(query));
    }
}
