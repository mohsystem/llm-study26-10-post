package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface SessionTokenRepository extends JpaRepository<SessionToken, Long> {
    Optional<SessionToken> findByToken(String token);
    void deleteByExpiresAtBefore(Instant instant);
}
