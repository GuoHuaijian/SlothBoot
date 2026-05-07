package com.sloth.boot.starter.auth.handler;

import cn.dev33.satoken.stp.StpInterface;
import com.sloth.boot.common.context.UserContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Sa-Token 权限/角色查询默认实现。
 * <p>
 * 从 {@link UserContext} 中读取角色信息。
 * 业务侧应替换此 Bean，从数据库或缓存中查询真实的权限和角色数据。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
public class DefaultStpInterface implements StpInterface {

    /**
     * 返回指定用户的所有权限码。
     * <p>
     * 默认实现返回空列表，业务侧需重写。
     *
     * @param loginId  用户 ID
     * @param loginType 账号类型
     * @return 权限码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    /**
     * 返回指定用户的所有角色码。
     * <p>
     * 默认从 UserContext 中读取角色集合。
     *
     * @param loginId   用户 ID
     * @param loginType 账号类型
     * @return 角色码列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Set<String> roles = UserContext.getRoles();
        return roles == null ? Collections.emptyList() : List.copyOf(roles);
    }
}
