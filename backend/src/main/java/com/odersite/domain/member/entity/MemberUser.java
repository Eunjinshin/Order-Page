package com.odersite.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "user_admin", nullable = false)
    @Builder.Default
    private Boolean userAdmin = false;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
