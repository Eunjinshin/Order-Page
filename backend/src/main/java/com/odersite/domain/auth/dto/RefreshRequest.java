package com.odersite.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshRequest {

    @NotBlank(message = "Refresh Token을 입력해주세요.")
    private String refreshToken;
}
