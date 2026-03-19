package com.odersite.domain.member.repository;

import com.odersite.domain.member.entity.MemberUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberUserRepository extends JpaRepository<MemberUser, Integer> {
}
