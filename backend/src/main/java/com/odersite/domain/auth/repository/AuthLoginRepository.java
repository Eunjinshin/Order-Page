package com.odersite.domain.auth.repository;

import com.odersite.domain.auth.entity.AuthLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthLoginRepository extends JpaRepository<AuthLogin, Integer> {

    Optional<AuthLogin> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmail(String email);
}
