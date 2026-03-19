package com.odersite.domain.member.controller;

import com.odersite.domain.member.dto.*;
import com.odersite.domain.member.service.MemberService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 API")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "로그인한 회원의 프로필 정보 (F-007)")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getMyProfile(userId(auth))));
    }

    @PatchMapping("/me/profile")
    @Operation(summary = "프로필 수정", description = "닉네임, 이름, 연락처 수정 (F-007)")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> updateProfile(
            Authentication auth,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.updateProfile(userId(auth), request)));
    }

    @GetMapping("/me/addresses")
    @Operation(summary = "배송지 목록 조회", description = "내 배송지 전체 조회 (F-007)")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getAddresses(userId(auth))));
    }

    @PostMapping("/me/addresses")
    @Operation(summary = "배송지 추가", description = "새 배송지 등록 (F-007)")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            Authentication auth,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.addAddress(userId(auth), request)));
    }

    @PutMapping("/me/addresses/{addressId}")
    @Operation(summary = "배송지 수정", description = "배송지 정보 수정 (F-007)")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            Authentication auth,
            @PathVariable Integer addressId,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.updateAddress(userId(auth), addressId, request)));
    }

    @DeleteMapping("/me/addresses/{addressId}")
    @Operation(summary = "배송지 삭제", description = "배송지 삭제 (F-007)")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            Authentication auth,
            @PathVariable Integer addressId) {
        memberService.deleteAddress(userId(auth), addressId);
        return ResponseEntity.ok(ApiResponse.ok("배송지가 삭제되었습니다.", null));
    }

    @PatchMapping("/me/addresses/{addressId}/default")
    @Operation(summary = "기본 배송지 설정", description = "선택한 배송지를 기본으로 설정 (F-007)")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            Authentication auth,
            @PathVariable Integer addressId) {
        memberService.setDefaultAddress(userId(auth), addressId);
        return ResponseEntity.ok(ApiResponse.ok("기본 배송지가 변경되었습니다.", null));
    }

    private Integer userId(Authentication auth) {
        return (Integer) auth.getPrincipal();
    }
}
