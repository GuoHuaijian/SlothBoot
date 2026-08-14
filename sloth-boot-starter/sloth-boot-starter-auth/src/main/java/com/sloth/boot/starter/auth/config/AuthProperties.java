package com.sloth.boot.starter.auth.config;

import com.sloth.boot.starter.auth.enums.DeviceStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证授权配置属性
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.auth")
public class AuthProperties {

    /**
     * 是否启用认证
     */
    private boolean enabled = true;

    /**
     * Token 名称（请求头中的 key）
     */
    private String tokenName = "Authorization";

    /**
     * Token 有效期（秒），默认 7200（2 小时）
     */
    private long tokenTimeout = 7200;

    /**
     * Token 最低活跃频率（秒），-1 表示不限
     */
    private long activeTimeout = -1;

    /**
     * 是否允许同一账号并发登录
     */
    private boolean isConcurrent = true;

    /**
     * 在多人登录同一账号时，是否共用同一个 Token
     */
    private boolean isShare = true;

    /**
     * Token 前缀
     */
    private String tokenPrefix = "Bearer";

    /**
     * 是否从 Cookie 中读取 Token
     */
    private boolean isReadCookie = false;

    /**
     * 是否从 Body 中读取 Token
     */
    private boolean isReadBody = false;

    /**
     * 白名单路径（不需要认证）
     */
    private List<String> whiteList = new ArrayList<>();

    /**
     * 黑名单路径（禁止访问）
     */
    private List<String> blackList = new ArrayList<>();

    /**
     * 多设备登录策略：ALLOW_MULTI / REPLACED / PROHIBIT。
     */
    private DeviceStrategy deviceStrategy = DeviceStrategy.ALLOW_MULTI;
}
