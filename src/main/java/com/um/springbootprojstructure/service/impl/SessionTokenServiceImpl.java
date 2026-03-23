package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.SessionTokenRepository;
import com.um.springbootprojstructure.service.SessionTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SessionTokenServiceImpl implements SessionTokenService {

    private final SessionTokenRepository sessionTokenRepository;
    private final long ttlSeconds;

    public SessionTokenServiceImpl(SessionTokenRepository sessionTokenRepository,
                                   @Value("${app.auth.session-ttl-seconds:86400}") long ttlSeconds) {
        this.sessionTokenRepository = sessionTokenRepository;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public SessionToken issueToken(User user) {
        sessionTokenRepository.deleteByExpiresAtBefore(Instant.now());

        String token = UUID.randomUUID().toString().replace("-", "");
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);

        SessionToken sessionToken = new SessionToken(token, user, expiresAt);
        return sessionTokenRepository.save(sessionToken);
    }

    @Override
    public Optional<SessionToken> findValid(String token) {
        return sessionTokenRepository.findByToken(token)
                .filter(st -> st.getExpiresAt().isAfter(Instant.now()));
    }
}
