package com.sloth.boot.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;

    private Long userId;

    private String username;
}
