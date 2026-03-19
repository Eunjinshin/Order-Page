package com.odersite.domain.auth.service;

import com.odersite.domain.auth.dto.*;
import com.odersite.domain.auth.entity.AuthLogin;
import com.odersite.domain.auth.repository.AuthLoginRepository;
import com.odersite.domain.member.entity.MemberProfile;
import com.odersite.domain.member.entity.MemberUser;
import com.odersite.domain.member.repository.MemberProfileRepository;
import com.odersite.domain.member.repository.MemberUserRepository;
import com.odersite.global.exception.BusinessException;
import com.odersite.global.exception.ErrorCode;
import com.odersite.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final String LOGIN_FAIL_PREFIX = "login:fail:";
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final String PASSWORD_RESET_PREFIX = "password:reset:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;

    private final MemberUserRepository memberUserRepository;
    private final AuthLoginRepository authLoginRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (authLoginRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }

        MemberUser user = MemberUser.builder()
                .nickname(request.getNickname())
                .userAdmin(false)
                .build();
        memberUserRepository.save(user);

        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
        AuthLogin authLogin = AuthLogin.builder()
                .memberUser(user)
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .refreshToken(refreshToken)
                .tokenExpiresAt(LocalDateTime.now().plusDays(30))
                .loginType(AuthLogin.LoginType.EMAIL)
                .build();
        authLoginRepository.save(authLogin);

        MemberProfile profile = MemberProfile.builder()
                .memberUser(user)
                .userName(request.getUserName())
                .userPhone(request.getUserPhone())
                .build();
        memberProfileRepository.save(profile);

        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), false);
        return new TokenResponse(accessToken, refreshToken, user.getUserId(), user.getNickname(), false);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        AuthLogin authLogin = authLoginRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        String lockKey = LOGIN_FAIL_PREFIX + request.getEmail();
        String failCount = redisTemplate.opsForValue().get(lockKey);

        if (failCount != null && Integer.parseInt(failCount) >= MAX_LOGIN_ATTEMPTS) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        if (!passwordEncoder.matches(request.getPassword(), authLogin.getPasswordHash())) {
            long count = redisTemplate.opsForValue().increment(lockKey);
            redisTemplate.expire(lockKey, LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
            log.warn("Login failed for email: {} (attempt {})", request.getEmail(), count);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        redisTemplate.delete(lockKey);

        String accessToken = jwtUtil.generateAccessToken(authLogin.getUserId(), authLogin.getMemberUser().getUserAdmin());
        String refreshToken = jwtUtil.generateRefreshToken(authLogin.getUserId());
        authLogin.updateRefreshToken(refreshToken, LocalDateTime.now().plusDays(30));

        return new TokenResponse(
                accessToken,
                refreshToken,
                authLogin.getUserId(),
                authLogin.getMemberUser().getNickname(),
                authLogin.getMemberUser().getUserAdmin()
        );
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        Integer userId = jwtUtil.getUserId(refreshToken);
        AuthLogin authLogin = authLoginRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!authLogin.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId, authLogin.getMemberUser().getUserAdmin());
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);
        authLogin.updateRefreshToken(newRefreshToken, LocalDateTime.now().plusDays(30));

        return new TokenResponse(
                newAccessToken,
                newRefreshToken,
                userId,
                authLogin.getMemberUser().getNickname(),
                authLogin.getMemberUser().getUserAdmin()
        );
    }

    @Transactional
    public void logout(Integer userId, String accessToken) {
        AuthLogin authLogin = authLoginRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // Refresh Token 무효화
        authLogin.updateRefreshToken("", LocalDateTime.now());

        // Access Token 블랙리스트 등록 (만료까지 Redis 보관)
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + accessToken,
                "logout",
                1L,
                TimeUnit.HOURS
        );
    }

    @Transactional
    public void withdraw(Integer userId) {
        AuthLogin authLogin = authLoginRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        authLogin.withdraw();
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        authLoginRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String resetToken = java.util.UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                PASSWORD_RESET_PREFIX + resetToken,
                request.getEmail(),
                24L,
                TimeUnit.HOURS
        );

        // TODO: 이메일 발송 서비스 연동 (EmailService)
        log.info("Password reset token generated for email: {} token: {}", request.getEmail(), resetToken);
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetRequest request) {
        String email = redisTemplate.opsForValue().get(PASSWORD_RESET_PREFIX + request.getToken());
        if (email == null) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        AuthLogin authLogin = authLoginRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        authLogin.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        redisTemplate.delete(PASSWORD_RESET_PREFIX + request.getToken());
    }
}
