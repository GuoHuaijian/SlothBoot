package com.sloth.boot.example.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sloth.boot.common.annotation.OperateLog;
import com.sloth.boot.common.enums.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.dto.LoginResponse;
import com.sloth.boot.example.dto.UserVO;
import com.sloth.boot.example.service.SystemDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemDemoService systemDemoService;

    @PostMapping("/login")
    public R<LoginResponse> login(@RequestParam Long userId, @RequestParam String username) {
        return R.ok(systemDemoService.login(userId, username));
    }

    @PostMapping("/logout")
    public R<String> logout() {
        systemDemoService.logout();
        return R.ok("登出成功");
    }

    @GetMapping("/current-user")
    public R<UserVO> getCurrentUser() {
        return R.ok(systemDemoService.getCurrentUser());
    }

    @OperateLog(module = "系统管理", description = "查询用户列表", type = OperateTypeEnum.QUERY)
    @GetMapping("/users")
    public R<List<UserVO>> getUsers() {
        return R.ok(systemDemoService.getUsers());
    }

    @OperateLog(module = "系统管理", description = "创建用户", type = OperateTypeEnum.CREATE)
    @PostMapping("/users")
    public R<UserVO> createUser(@RequestBody UserVO user) {
        return R.ok(systemDemoService.createUser(user));
    }

    @SaCheckPermission("user:view")
    @GetMapping("/permissions")
    public R<String> checkPermission() {
        return R.ok("你有 user:view 权限");
    }

    @GetMapping("/data-scope")
    public R<String> getDataScope() {
        return R.ok(systemDemoService.getDataScope());
    }
}
