package com.sloth.boot.example.dto;

import com.sloth.boot.common.annotation.Desensitize;
import com.sloth.boot.common.annotation.DesensitizeType;
import lombok.Data;

import java.util.Set;

@Data
public class UserVO {

    private Long id;

    private String username;

    @Desensitize(type = DesensitizeType.MOBILE)
    private String phone;

    @Desensitize(type = DesensitizeType.ID_CARD)
    private String idCard;

    @Desensitize(type = DesensitizeType.EMAIL)
    private String email;

    private Set<String> roles;
}
