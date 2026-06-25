package com.sloth.boot.example.adapter.controller.user;

import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.model.query.user.UserPageQry;
import com.sloth.boot.example.application.model.vo.user.SysUserVO;
import com.sloth.boot.example.application.query.user.PageUserPermissionQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户数据权限分页查询接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PageUserPermissionController {

    private final PageUserPermissionQuery userPermissionPageQuery;

    @Operation(summary = "数据权限查询（用户）")
    @GetMapping("/scope")
    public R<PageResult<SysUserVO>> pageUserPermission(UserPageQry query) {
        return R.ok(userPermissionPageQuery.execute(query));
    }
}
