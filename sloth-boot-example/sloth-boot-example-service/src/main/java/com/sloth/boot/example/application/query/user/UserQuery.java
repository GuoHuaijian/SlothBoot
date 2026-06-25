package com.sloth.boot.example.application.query.user;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.model.convert.user.UserConvert;
import com.sloth.boot.example.application.model.enums.user.UserErrorCode;
import com.sloth.boot.example.application.model.vo.user.SysUserVO;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.example.infrastructure.repository.mapper.user.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户详情查询。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class UserQuery {

    private final SysUserMapper sysUserMapper;
    private final UserConvert userConvert;

    /**
     * 执行用户详情查询。
     *
     * @param id 用户ID
     * @return 用户视图对象（敏感字段自动脱敏）
     * @throws BizException 当用户不存在时抛出
     */
    public SysUserVO execute(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw BizException.of(UserErrorCode.USER_NOT_FOUND, "用户不存在: " + id);
        }
        return userConvert.toVO(user);
    }
}
