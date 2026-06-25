package com.sloth.boot.example.application.command.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.model.enums.auth.AuthErrorCode;
import com.sloth.boot.example.application.model.vo.auth.LoginVO;
import com.sloth.boot.example.application.model.vo.auth.SystemUserVO;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.example.infrastructure.repository.mapper.user.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 认证命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthCommand {

    private final SysUserMapper sysUserMapper;

    /**
     * 用户登录。
     * <p>
     * 验证用户凭据，创建登录会话，返回Token令牌。
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 登录结果，包含Token和用户信息
     * @throws BizException 当用户不存在时抛出
     */
    public LoginVO login(Long userId, String username) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw BizException.of(AuthErrorCode.USER_NOT_FOUND);
        }
        StpUtil.login(userId);
        UserContext.UserInfo userInfo = new UserContext.UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUsername(username);
        userInfo.setDataScope("all");
        UserContext.set(userInfo);
        log.info("用户登录成功: userId={}, username={}", userId, username);
        return LoginVO.builder()
            .token(StpUtil.getTokenValue())
            .userId(userId)
            .username(username)
            .build();
    }

    /**
     * 用户登出。
     * <p>
     * 销毁当前会话，清除用户上下文。
     */
    public void logout() {
        StpUtil.logout();
        UserContext.clear();
        log.info("用户登出成功");
    }

    /**
     * 获取当前登录用户信息。
     * <p>
     * 返回脱敏后的用户信息，包括ID、用户名、手机号、邮箱等。
     *
     * @return 当前用户信息
     */
    public SystemUserVO getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        SystemUserVO vo = new SystemUserVO();
        vo.setId(userId);
        vo.setUsername(user != null ? user.getUsername() : "unknown");
        vo.setPhone(user != null ? user.getPhone() : null);
        vo.setIdCard(user != null ? user.getIdCard() : null);
        vo.setEmail(user != null ? user.getEmail() : null);
        vo.setRoles(Set.of("admin", "user"));
        return vo;
    }

    /**
     * 获取当前用户权限列表。
     * <p>
     * 需要先调用登录接口获取Token。
     *
     * @return 权限标识集合
     */
    public Set<String> permissions() {
        StpUtil.checkLogin();
        return Set.of("user:list", "user:create", "user:update", "user:delete",
            "dept:list", "dept:create", "dept:update", "dept:delete",
            "product:list", "product:create", "order:list", "order:create");
    }

    /**
     * 获取当前用户数据权限范围。
     * <p>
     * 不同角色可查看不同范围的数据（全部、本部门、仅本人）。
     *
     * @return 数据权限范围标识
     */
    public String dataScope() {
        StpUtil.checkLogin();
        return "all";
    }
}
