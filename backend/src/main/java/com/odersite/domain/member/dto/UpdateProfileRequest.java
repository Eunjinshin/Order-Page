package com.odersite.domain.member.dto;

import lombok.Getter;

@Getter
public class UpdateProfileRequest {
    private String nickname;
    private String userName;
    private String userPhone;
}
