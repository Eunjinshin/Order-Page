package com.odersite.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_PROFILE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Integer profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MemberUser memberUser;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "user_phone", length = 20)
    private String userPhone;

    public void update(String userName, String userPhone) {
        this.userName = userName;
        this.userPhone = userPhone;
    }
}
