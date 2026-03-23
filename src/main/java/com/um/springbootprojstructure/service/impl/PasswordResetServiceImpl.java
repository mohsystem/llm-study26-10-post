package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.entity.PasswordResetToken;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.PasswordResetTokenRepository;
import com.um.springbootprojstructure.repository.UserRepository;
import com.um.springbootprojstructure.service.PasswordResetService;
import com.um.springbootprojstructure.service.exception.BadRequestException;
import com.um.springbootprojstructure.service.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final long ttlSeconds;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetTokenRepository tokenRepository,
                                    PasswordEncoder passwordEncoder,
                                    @Value("${app.auth.reset-token-ttl-seconds:900}") long ttlSeconds) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public String initiateReset(String identifier) {
        tokenRepository.deleteByExpiresAtBefore(Instant.now());

        Optional<User> userOpt = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier));

        // Generic response to avoid account enumeration
        if (userOpt.isEmpty()) {
            return "RESET_REQUEST_ACCEPTED";
        }

        User user = userOpt.get();

        String token = UUID.randomUUID().toString().replace("-", "");
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);

        tokenRepository.save(new PasswordResetToken(token, user, expiresAt));

        // no UI/email: token can be logged for testing (optional as previously described)
        return "RESET_REQUEST_ACCEPTED";
    }

    @Override
    public String confirmReset(String token, String newPassword) {
        tokenRepository.deleteByExpiresAtBefore(Instant.now());

        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("INVALID_RESET_TOKEN", "Invalid or expired reset token"));

        if (prt.isUsed()) {
            throw new BadRequestException("RESET_TOKEN_USED", "Reset token already used");
        }
        if (!prt.getExpiresAt().isAfter(Instant.now())) {
            throw new UnauthorizedException("INVALID_RESET_TOKEN", "Invalid or expired reset token");
        }

        User user = prt.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        prt.setUsed(true);
        tokenRepository.save(prt);

        return "PASSWORD_RESET";
    }
}
