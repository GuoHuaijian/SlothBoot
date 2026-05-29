package com.sloth.boot.example.service.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.example.domain.entity.SysUser;
import com.sloth.boot.example.domain.mapper.SysUserMapper;
import com.sloth.boot.example.model.system.vo.LoginResponse;
import com.sloth.boot.example.model.system.vo.SystemUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 认证授权演示服务 - 展示 Sa-Token 认证授权、数据脱敏、数据权限等能力
 * <p>
 * 使用 SysUserMapper 从数据库读取用户数据。
 * 登录时校验用户是否存在，getCurrentUser 从数据库查询并转换为脱敏 VO。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthDemoService {

    private final SysUserMapper userMapper;

    /**
     * 用户登录 - 从数据库验证用户后使用 Sa-Token 颁发令牌
     *
     * @param userId   用户ID
     * @param username 用户名（仅用于日志记录）
     * @return 登录响应（含 token）
     * @throws IllegalArgumentException 用户不存在时抛出
     */
    public LoginResponse login(Long userId, String username) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + userId);
        }
        StpUtil.login(userId);
        // 设置 UserContext（数据权限演示依赖此上下文）
        UserContext.UserInfo userInfo = new UserContext.UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUsername(user.getUsername());
        userInfo.setDataScope("all");
        UserContext.set(userInfo);

        log.info("用户登录成功: userId={}, username={}", userId, user.getUsername());
        return LoginResponse.builder()
                .token(StpUtil.getTokenValue())
                .userId(userId)
                .username(user.getUsername())
                .build();
    }

    /**
     * 用户登出 - 清除 Sa-Token 登录状态和 UserContext
     */
    public void logout() {
        StpUtil.logout();
        UserContext.clear();
        log.info("用户登出成功");
    }

    /**
     * 获取当前登录用户信息（含数据脱敏）
     * <p>
     * 从数据库查询 SysUser，转换为 SystemUserVO（@Desensitize 自动脱敏 phone/idCard/email）。
     *
     * @return 脱敏后的用户信息
     * @throws IllegalStateException 未登录或用户不存在时抛出
     */
    public SystemUserVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalStateException("当前登录用户不存在: " + userId);
        }
        return toSystemUserVO(user);
    }

    /**
     * 获取当前用户数据权限范围
     *
     * @return 数据权限范围描述
     */
    public String getDataScope() {
        String dataScope = UserContext.getDataScope();
        return dataScope != null ? dataScope : "全部数据";
    }

    /**
     * SysUser -> SystemUserVO 转换
     * <p>
     * 从 extraInfo JSON 中提取 roles 字段。
     *
     * @param user 数据库用户实体
     * @return 系统用户 VO（@Desensitize 自动脱敏）
     */
    @SuppressWarnings("unchecked")
    private SystemUserVO toSystemUserVO(SysUser user) {
        SystemUserVO vo = new SystemUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setIdCard(user.getIdCard());
        vo.setEmail(user.getEmail());
        // 从 extraInfo 提取 roles，无则默认 ["user"]
        Set<String> roles = new HashSet<>(Set.of("user"));
        if (user.getExtraInfo() != null) {
            Object rolesObj = user.getExtraInfo().get("roles");
            if (rolesObj instanceof List<?> roleList) {
                roles = new HashSet<>();
                for (Object r : roleList) {
                    roles.add(String.valueOf(r));
                }
            }
        }
        vo.setRoles(roles);
        return vo;
    }
}
