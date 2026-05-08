package com.sloth.boot.example.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CryptoResponse {

    private String result;

    private String original;

    private String processed;

    private Long costMs;

    private Boolean verified;

    private String publicKey;

    private String privateKey;

    private String sign;

    private Long timestamp;

    private String nonce;
}
