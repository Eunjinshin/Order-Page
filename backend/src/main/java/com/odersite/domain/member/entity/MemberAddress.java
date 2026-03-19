package com.odersite.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_ADDRESS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MemberUser memberUser;

    @Column(name = "address_name", length = 50)
    private String addressName;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    public void update(String addressName, String address, String zipCode, String detailAddress) {
        this.addressName = addressName;
        this.address = address;
        this.zipCode = zipCode;
        this.detailAddress = detailAddress;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
