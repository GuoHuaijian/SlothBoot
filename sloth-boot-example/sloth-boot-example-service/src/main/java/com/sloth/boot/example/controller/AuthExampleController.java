package com.sloth.boot.example.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.common.result.R;
import com.sloth.boot.starter.auth.handler.SaTokenContextHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证授权示例控制器。
 * <p>
 * 展示 Sa-Token 登录/登出/鉴权的使用方式。
 * 前提：已引入 sloth-boot-starter-auth 依赖。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthExampleController {

    private final SaTokenContextHandler saTokenContextHandler;

    /**
     * 登录示例
     * <p>
     * 使用 Sa-Token 的 StpUtil.login() 完成登录，返回 Token。
     * 业务侧应先校验用户名密码，此处为简化示例。
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestParam Long userId, @RequestParam String username) {
        // 执行登录（实际项目中应先校验密码）
        StpUtil.login(userId);

        // 同步到 UserContext
        saTokenContextHandler.syncToUserContext();

        return R.ok(Map.of(
                "token", StpUtil.getTokenValue(),
                "userId", userId,
                "username", username
        ));
    }

    /**
     * 登出示例
     */
    @PostMapping("/logout")
    public R<String> logout() {
        StpUtil.logout();
        saTokenContextHandler.clearUserContext();
        return R.ok("登出成功");
    }

    /**
     * 获取当前登录信息
     */
    @GetMapping("/info")
    public R<Map<String, Object>> info() {
        if (!StpUtil.isLogin()) {
            return R.fail(401, "未登录");
        }
        return R.ok(Map.of(
                "loginId", StpUtil.getLoginId(),
                "tokenTimeout", StpUtil.getTokenTimeout(),
                "tokenName", StpUtil.getTokenName()
        ));
    }

    /**
     * 权限校验示例
     * <p>
     * 需要 "user:edit" 权限才能访问。
     * 使用 Sa-Token 的注解式鉴权。
     */
    @GetMapping("/need-permission")
    @cn.dev33.satoken.annotation.SaCheckPermission("user:edit")
    public R<String> needPermission() {
        return R.ok("你有 user:edit 权限");
    }

    /**
     * 角色校验示例
     * <p>
     * 需要 "admin" 角色才能访问。
     */
    @GetMapping("/need-role")
    @cn.dev33.satoken.annotation.SaCheckRole("admin")
    public R<String> needRole() {
        return R.ok("你有 admin 角色");
    }
}
