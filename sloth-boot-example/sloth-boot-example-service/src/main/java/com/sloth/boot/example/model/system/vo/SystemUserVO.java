package com.sloth.boot.example.model.system.vo;

import com.sloth.boot.common.security.desensitize.Desensitize;
import com.sloth.boot.common.security.desensitize.DesensitizeType;
import lombok.Data;

import java.util.Set;

@Data
public class SystemUserVO {

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
