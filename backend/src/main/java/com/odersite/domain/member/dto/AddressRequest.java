package com.odersite.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AddressRequest {

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "우편번호를 입력해주세요.")
    private String zipCode;

    private String addressName;
    private String detailAddress;
    private boolean isDefault;
}
