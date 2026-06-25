package com.sloth.boot.example.application.command.user;

import com.sloth.boot.common.exception.BizException;
import com.sloth.boot.example.application.model.enums.user.UserErrorCode;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.example.infrastructure.repository.mapper.user.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 删除用户命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveUserCommand {

    private final SysUserMapper sysUserMapper;

    /**
     * 执行删除用户操作。
     * <p>
     * 根据用户ID删除用户记录（物理删除）。
     *
     * @param id 用户ID
     * @throws BizException 当用户不存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) throw BizException.of(UserErrorCode.USER_NOT_FOUND, "用户不存在: " + id);
        sysUserMapper.deleteById(id);
        log.info("删除用户成功: id={}", id);
    }
}
