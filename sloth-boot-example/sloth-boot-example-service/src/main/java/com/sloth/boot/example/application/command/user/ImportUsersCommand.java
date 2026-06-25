package com.sloth.boot.example.application.command.user;

import com.sloth.boot.example.application.model.convert.user.UserConvert;
import com.sloth.boot.example.application.model.form.user.UserCreateForm;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.example.infrastructure.repository.mapper.user.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 批量导入用户命令。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImportUsersCommand {

    private final SysUserMapper sysUserMapper;
    private final UserConvert userConvert;

    /**
     * 执行批量导入用户操作。
     * <p>
     * 将用户表单列表转换为实体列表并批量插入数据库。
     *
     * @param forms 用户创建表单列表
     * @return 成功导入的用户数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int execute(List<UserCreateForm> forms) {
        if (forms == null || forms.isEmpty()) {
            return 0;
        }
        List<SysUser> users = userConvert.toEntityList(forms);
        sysUserMapper.insertBatch(users);
        log.info("批量导入用户成功: count={}", users.size());
        return users.size();
    }
}
