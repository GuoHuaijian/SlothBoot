package com.sloth.boot.example.controller.auth;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.model.system.request.LoginRequest;
import com.sloth.boot.example.model.system.vo.LoginResponse;
import com.sloth.boot.example.model.system.vo.SystemUserVO;
import com.sloth.boot.example.service.auth.AuthDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证授权演示接口
 * <p>
 * 演示 Sa-Token 认证授权（登录/登出/权限校验）、数据脱敏 (@Desensitize)、数据权限 (UserContext) 等能力。
 * 用户数据统一从 SysUserMapper（H2 数据库）读取。
 */
@Tag(name = "认证授权", description = "演示 Sa-Token 认证授权、数据脱敏、数据权限等能力")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthDemoService authDemoService;

    /**
     * 用户登录
     * <p>
     * 从数据库验证用户存在后，使用 Sa-Token 颁发令牌并设置 UserContext。
     */
    @Operation(summary = "用户登录", description = "从数据库验证用户后使用 Sa-Token 颁发登录令牌，同时设置 UserContext 用于数据权限演示")
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(authDemoService.login(request.getUserId(), request.getUsername()));
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "注销当前用户的登录状态，清除 UserContext")
    @PostMapping("/logout")
    public R<String> logout() {
        authDemoService.logout();
        return R.ok("登出成功");
    }

    /**
     * 获取当前用户
     * <p>
     * 从数据库查询并返回脱敏后的用户信息（phone/idCard/email 自动脱敏）。
     */
    @Operation(summary = "获取当前用户", description = "获取当前登录用户的详细信息（含 @Desensitize 脱敏字段），未登录返回空数据")
    @GetMapping("/current-user")
    public R<SystemUserVO> getCurrentUser() {
        return R.ok(authDemoService.getCurrentUser());
    }

    /**
     * 权限校验
     * <p>
     * 演示 @SaCheckPermission 注解：需要 user:view 权限才能访问。
     */
    @Operation(summary = "权限校验", description = "演示 @SaCheckPermission 注解，需登录且拥有 user:view 权限")
    @SaCheckPermission("user:view")
    @GetMapping("/permissions")
    public R<String> checkPermission() {
        return R.ok("你有 user:view 权限");
    }

    /**
     * 数据权限
     * <p>
     * 演示 UserContext.getDataScope()，需先登录设置上下文。
     */
    @Operation(summary = "数据权限", description = "获取当前用户的数据权限范围（需先登录）")
    @GetMapping("/data-scope")
    public R<String> getDataScope() {
        return R.ok(authDemoService.getDataScope());
    }
}
