package com.sloth.boot.example.service;

import cn.dev33.satoken.stp.StpUtil;
import com.sloth.boot.common.context.UserContext;
import com.sloth.boot.example.dto.LoginResponse;
import com.sloth.boot.example.dto.UserVO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 系统演示服务 - 展示 Sa-Token 认证、数据脱敏等能力
 */
@Slf4j
@Service
public class SystemDemoService {

    /** 内存用户存储 */
    private final ConcurrentHashMap<Long, UserVO> userMap = new ConcurrentHashMap<>();

    /** 自增ID生成器 */
    private final AtomicLong idGenerator = new AtomicLong(3);

    @PostConstruct
    public void init() {
        // admin 用户
        UserVO admin = new UserVO();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPhone("13800138000");
        admin.setIdCard("110101199001011234");
        admin.setEmail("admin@slothboot.com");
        admin.setRoles(Set.of("admin", "user"));

        // 普通用户
        UserVO user = new UserVO();
        user.setId(2L);
        user.setUsername("user");
        user.setPhone("13900139000");
        user.setIdCard("110101199502025678");
        user.setEmail("user@slothboot.com");
        user.setRoles(Set.of("user"));

        // 访客
        UserVO guest = new UserVO();
        guest.setId(3L);
        guest.setUsername("guest");
        guest.setPhone("13700137000");
        guest.setIdCard("110101200003039012");
        guest.setEmail("guest@slothboot.com");
        guest.setRoles(Set.of("guest"));

        userMap.put(1L, admin);
        userMap.put(2L, user);
        userMap.put(3L, guest);

        log.info("系统演示数据初始化完成, 共 {} 个用户", userMap.size());
    }

    /**
     * 用户登录 - 使用 Sa-Token 颁发令牌
     */
    public LoginResponse login(Long userId, String username) {
        StpUtil.login(userId);
        log.info("用户登录成功: userId={}, username={}", userId, username);
        return LoginResponse.builder()
                .token(StpUtil.getTokenValue())
                .userId(userId)
                .username(username)
                .build();
    }

    /**
     * 用户登出
     */
    public void logout() {
        StpUtil.logout();
        log.info("用户登出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    public UserVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        return userMap.get(userId);
    }

    /**
     * 获取全部用户列表
     */
    public List<UserVO> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    /**
     * 创建用户（自动分配ID）
     */
    public UserVO createUser(UserVO user) {
        Long id = idGenerator.incrementAndGet();
        user.setId(id);
        userMap.put(id, user);
        log.info("创建用户成功: id={}, username={}", id, user.getUsername());
        return user;
    }

    /**
     * 获取当前用户数据权限范围
     */
    public String getDataScope() {
        String dataScope = UserContext.getDataScope();
        return dataScope != null ? dataScope : "全部数据";
    }
}
