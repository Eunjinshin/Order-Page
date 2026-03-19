package com.odersite.domain.member.dto;

import com.odersite.domain.member.entity.MemberProfile;
import com.odersite.domain.member.entity.MemberUser;
import lombok.Getter;

@Getter
public class MemberProfileResponse {
    private final Integer userId;
    private final String nickname;
    private final boolean userAdmin;
    private final String userName;
    private final String userPhone;

    public MemberProfileResponse(MemberUser user, MemberProfile profile) {
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.userAdmin = user.getUserAdmin();
        this.userName = profile != null ? profile.getUserName() : null;
        this.userPhone = profile != null ? profile.getUserPhone() : null;
    }
}
