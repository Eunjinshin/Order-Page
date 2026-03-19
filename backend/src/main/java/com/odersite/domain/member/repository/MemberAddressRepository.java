package com.odersite.domain.member.repository;

import com.odersite.domain.member.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Integer> {

    List<MemberAddress> findByMemberUser_UserId(Integer userId);

    @Modifying
    @Query("UPDATE MemberAddress a SET a.isDefault = false WHERE a.memberUser.userId = :userId")
    void clearDefaultByUserId(Integer userId);
}
