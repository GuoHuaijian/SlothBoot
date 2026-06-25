package com.sloth.boot.example.adapter.controller.auth;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.auth.AuthCommand;
import com.sloth.boot.example.application.model.form.auth.LoginForm;
import com.sloth.boot.example.application.model.vo.auth.LoginVO;
import com.sloth.boot.example.application.model.vo.auth.SystemUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 认证接口。
 * <p>
 * 演示 Sa-Token 认证：登录、登出、获取当前用户、权限校验、数据权限范围。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "认证", description = "登录、登出、Token演示")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommand authCommand;

    @Operation(summary = "登录", description = "使用Sa-Token登录，返回Token令牌")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginForm form) {
        return R.ok(authCommand.login(form.getUserId(), form.getUsername()));
    }

    @Operation(summary = "登出", description = "注销当前会话")
    @PostMapping("/logout")
    public R<Void> logout() {
        authCommand.logout();
        return R.ok();
    }

    @Operation(summary = "获取当前用户", description = "返回脱敏后的用户信息")
    @GetMapping("/current-user")
    public R<SystemUserVO> currentUser() {
        return R.ok(authCommand.getCurrentUser());
    }

    @Operation(summary = "获取当前用户权限列表", description = "演示 @SaCheckPermission 权限校验，先调登录接口")
    @GetMapping("/permissions")
    public R<Set<String>> permissions() {
        return R.ok(authCommand.permissions());
    }

    @Operation(summary = "获取当前用户数据范围", description = "演示不同角色查看不同范围数据（全部、本部门、仅本人）")
    @GetMapping("/data-scope")
    public R<String> dataScope() {
        return R.ok(authCommand.dataScope());
    }
}
