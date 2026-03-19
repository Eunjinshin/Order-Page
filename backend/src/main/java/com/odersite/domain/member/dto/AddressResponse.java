package com.odersite.domain.member.dto;

import com.odersite.domain.member.entity.MemberAddress;
import lombok.Getter;

@Getter
public class AddressResponse {
    private final Integer addressId;
    private final String addressName;
    private final String address;
    private final String zipCode;
    private final String detailAddress;
    private final boolean isDefault;

    public AddressResponse(MemberAddress a) {
        this.addressId = a.getAddressId();
        this.addressName = a.getAddressName();
        this.address = a.getAddress();
        this.zipCode = a.getZipCode();
        this.detailAddress = a.getDetailAddress();
        this.isDefault = a.getIsDefault();
    }
}
