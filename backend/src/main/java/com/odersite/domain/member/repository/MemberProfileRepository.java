package com.odersite.domain.member.repository;

import com.odersite.domain.member.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Integer> {
    Optional<MemberProfile> findByMemberUser_UserId(Integer userId);
}
