package com.sloth.boot.example.application.command.user;

import com.sloth.boot.example.application.model.convert.user.UserConvert;
import com.sloth.boot.example.application.model.form.user.UserCreateForm;
import com.sloth.boot.example.infrastructure.model.po.user.SysUser;
import com.sloth.boot.example.infrastructure.repository.mapper.user.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 创建用户命令单元测试。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RegisterUserCommandTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private UserConvert userConvert;

    @InjectMocks
    private RegisterUserCommand registerUserCommand;

    @Test
    void testExecuteSuccess() {
        // 准备测试数据
        UserCreateForm form = new UserCreateForm();
        form.setUsername("testuser");
        form.setPhone("13800138000");
        form.setEmail("test@example.com");

        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");

        when(userConvert.toEntity(form)).thenReturn(user);
        when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

        // 执行测试
        Long userId = registerUserCommand.execute(form);

        // 验证结果
        assertEquals(1L, userId);
        assertEquals("testuser", user.getUsername());
        verify(userConvert).toEntity(form);
        verify(sysUserMapper).insert(user);
    }

    @Test
    void testExecuteWithMinimalData() {
        // 准备测试数据 - 只有必填字段
        UserCreateForm form = new UserCreateForm();
        form.setUsername("minimaluser");

        SysUser user = new SysUser();
        user.setId(2L);
        user.setUsername("minimaluser");

        when(userConvert.toEntity(form)).thenReturn(user);
        when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

        // 执行测试
        Long userId = registerUserCommand.execute(form);

        // 验证结果
        assertEquals(2L, userId);
        verify(userConvert).toEntity(form);
        verify(sysUserMapper).insert(user);
    }
}
