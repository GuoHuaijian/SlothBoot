package com.sloth.boot.example.application.command.user;

import com.sloth.boot.example.application.model.convert.user.UserConvert;
import com.sloth.boot.example.application.model.form.user.UserCreateForm;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.example.infrastructure.repository.mapper.user.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 创建用户命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterUserCommand {

    private final SysUserMapper sysUserMapper;
    private final UserConvert userConvert;

    /**
     * 执行创建用户操作。
     * <p>
     * 将用户表单数据转换为实体并持久化到数据库。
     *
     * @param form 用户创建表单
     * @return 创建成功的用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long execute(UserCreateForm form) {
        SysUser user = userConvert.toEntity(form);
        sysUserMapper.insert(user);
        log.info("创建用户成功: id={}, username={}", user.getId(), user.getUsername());
        return user.getId();
    }
}
