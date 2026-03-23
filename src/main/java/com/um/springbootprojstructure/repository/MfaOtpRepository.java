package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.MfaOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface MfaOtpRepository extends JpaRepository<MfaOtp, Long> {
    Optional<MfaOtp> findTopBySessionToken_TokenOrderByCreatedAtDesc(String sessionToken);
    void deleteByExpiresAtBefore(Instant now);
}
