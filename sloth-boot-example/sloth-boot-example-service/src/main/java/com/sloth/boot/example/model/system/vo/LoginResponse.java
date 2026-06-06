package com.sloth.boot.example.model.system.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应结果
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@Builder
public class LoginResponse {

    private String token;

    private Long userId;

    private String username;
}
