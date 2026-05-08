package com.sloth.boot.example.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CryptoRequest {

    private String data;

    private String key;

    private String iv;

    private String publicKey;

    private String privateKey;

    private String sign;

    private Map<String, Object> params;

    private Long timestamp;

    private String nonce;

    private String secretKey;

    private String content;
}
