package com.sloth.boot.example.application.command.user;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.model.convert.user.UserConvert;
import com.sloth.boot.example.application.model.enums.user.UserErrorCode;
import com.sloth.boot.example.application.model.form.user.UserUpdateForm;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.example.infrastructure.repository.mapper.user.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 更新用户命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModifyUserCommand {

    private final SysUserMapper sysUserMapper;
    private final UserConvert userConvert;

    /**
     * 执行更新用户操作。
     * <p>
     * 根据用户ID查询现有用户，使用表单数据更新用户信息。
     *
     * @param form 用户更新表单
     * @throws BizException 当用户不存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute(UserUpdateForm form) {
        SysUser existing = sysUserMapper.selectById(form.getId());
        if (existing == null) {
            throw BizException.of(UserErrorCode.USER_NOT_FOUND, "用户不存在: " + form.getId());
        }
        userConvert.updateEntity(form, existing);
        sysUserMapper.updateById(existing);
        log.info("更新用户成功: id={}", form.getId());
    }
}
