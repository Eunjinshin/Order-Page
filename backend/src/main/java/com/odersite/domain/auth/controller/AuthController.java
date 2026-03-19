package com.odersite.domain.auth.controller;

import com.odersite.domain.auth.dto.*;
import com.odersite.domain.auth.service.AuthService;
import com.odersite.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "이메일 회원가입", description = "이메일/비밀번호로 회원가입 (F-001, F-002)")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@Valid @RequestBody SignupRequest request) {
        TokenResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.ok("회원가입이 완료되었습니다.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "이메일 로그인", description = "이메일/비밀번호 로그인, 5회 실패 시 30분 잠금 (F-003)")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("로그인 성공.", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access Token 재발급 (F-005)")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok("토큰이 재발급되었습니다.", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "토큰 무효화 (F-008)")
    public ResponseEntity<ApiResponse<Void>> logout(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = (Integer) authentication.getPrincipal();
        String accessToken = authHeader.substring(7);
        authService.logout(userId, accessToken);
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 되었습니다.", null));
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원탈퇴", description = "30일 후 소프트 딜리트 (F-008)")
    public ResponseEntity<ApiResponse<Void>> withdraw(Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        authService.withdraw(userId);
        return ResponseEntity.ok(ApiResponse.ok("회원탈퇴가 처리되었습니다.", null));
    }

    @PostMapping("/password-reset/request")
    @Operation(summary = "비밀번호 재설정 요청", description = "이메일로 재설정 링크 발송 (F-006)")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호 재설정 이메일을 발송했습니다.", null));
    }

    @PostMapping("/password-reset/confirm")
    @Operation(summary = "비밀번호 재설정 확인", description = "토큰 검증 후 비밀번호 변경 (F-006)")
    public ResponseEntity<ApiResponse<Void>> confirmPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        authService.confirmPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호가 변경되었습니다.", null));
    }
}
