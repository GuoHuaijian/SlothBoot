package com.sloth.boot.example.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private Long userId;

    private String username;
}
