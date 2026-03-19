package com.odersite.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Integer userId;
    private String nickname;
    private boolean isAdmin;
}
