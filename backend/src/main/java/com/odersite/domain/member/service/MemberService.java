package com.odersite.domain.member.service;

import com.odersite.domain.member.dto.*;
import com.odersite.domain.member.entity.MemberAddress;
import com.odersite.domain.member.entity.MemberProfile;
import com.odersite.domain.member.entity.MemberUser;
import com.odersite.domain.member.repository.MemberAddressRepository;
import com.odersite.domain.member.repository.MemberProfileRepository;
import com.odersite.domain.member.repository.MemberUserRepository;
import com.odersite.global.exception.BusinessException;
import com.odersite.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberUserRepository memberUserRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final MemberAddressRepository memberAddressRepository;

    public MemberProfileResponse getMyProfile(Integer userId) {
        MemberUser user = findUser(userId);
        MemberProfile profile = memberProfileRepository.findByMemberUser_UserId(userId).orElse(null);
        return new MemberProfileResponse(user, profile);
    }

    @Transactional
    public MemberProfileResponse updateProfile(Integer userId, UpdateProfileRequest request) {
        MemberUser user = findUser(userId);
        if (request.getNickname() != null) {
            user.updateNickname(request.getNickname());
        }

        MemberProfile profile = memberProfileRepository.findByMemberUser_UserId(userId)
                .orElseGet(() -> memberProfileRepository.save(
                        MemberProfile.builder().memberUser(user).build()
                ));
        profile.update(request.getUserName(), request.getUserPhone());

        return new MemberProfileResponse(user, profile);
    }

    public List<AddressResponse> getAddresses(Integer userId) {
        return memberAddressRepository.findByMemberUser_UserId(userId)
                .stream().map(AddressResponse::new).toList();
    }

    @Transactional
    public AddressResponse addAddress(Integer userId, AddressRequest request) {
        MemberUser user = findUser(userId);

        if (request.isDefault()) {
            memberAddressRepository.clearDefaultByUserId(userId);
        }

        MemberAddress address = MemberAddress.builder()
                .memberUser(user)
                .addressName(request.getAddressName())
                .address(request.getAddress())
                .zipCode(request.getZipCode())
                .detailAddress(request.getDetailAddress())
                .isDefault(request.isDefault())
                .build();

        return new AddressResponse(memberAddressRepository.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(Integer userId, Integer addressId, AddressRequest request) {
        MemberAddress address = findAddress(userId, addressId);

        if (request.isDefault()) {
            memberAddressRepository.clearDefaultByUserId(userId);
        }

        address.update(request.getAddressName(), request.getAddress(), request.getZipCode(), request.getDetailAddress());
        address.setDefault(request.isDefault());
        return new AddressResponse(address);
    }

    @Transactional
    public void deleteAddress(Integer userId, Integer addressId) {
        MemberAddress address = findAddress(userId, addressId);
        memberAddressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(Integer userId, Integer addressId) {
        findAddress(userId, addressId);
        memberAddressRepository.clearDefaultByUserId(userId);
        MemberAddress address = findAddress(userId, addressId);
        address.setDefault(true);
    }

    private MemberUser findUser(Integer userId) {
        return memberUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private MemberAddress findAddress(Integer userId, Integer addressId) {
        MemberAddress address = memberAddressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!address.getMemberUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return address;
    }
}
