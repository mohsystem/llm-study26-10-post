package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.dto.ChangePasswordRequest;
import com.um.springbootprojstructure.dto.LoginRequest;
import com.um.springbootprojstructure.dto.RegisterRequest;
import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.Role;
import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.UserRepository;
import com.um.springbootprojstructure.service.AuthService;
import com.um.springbootprojstructure.service.SessionTokenService;
import com.um.springbootprojstructure.service.exception.DuplicateAccountException;
import com.um.springbootprojstructure.service.exception.NotFoundException;
import com.um.springbootprojstructure.service.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionTokenService sessionTokenService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           SessionTokenService sessionTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionTokenService = sessionTokenService;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateAccountException("DUPLICATE_USERNAME", "Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateAccountException("DUPLICATE_EMAIL", "Email already exists: " + request.getEmail());
        }

        String hash = passwordEncoder.encode(request.getPassword());

        // fullName not provided by requirements; keep as username for now.
        User user = new User(request.getUsername(), request.getUsername(), request.getEmail(), hash);
        user.setRole(Role.USER);
        user.setStatus(AccountStatus.ACTIVE);

        return userRepository.save(user);
    }

    @Override
    public SessionToken login(LoginRequest request) {
        String identifier = request.getIdentifier();

        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS", "Invalid credentials"));

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new UnauthorizedException("USER_INACTIVE", "User is inactive");
        }

        boolean ok = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!ok) {
            throw new UnauthorizedException("INVALID_CREDENTIALS", "Invalid credentials");
        }

        return sessionTokenService.issueToken(user);
    }

    @Override
    public void changePassword(Long authenticatedUserId, ChangePasswordRequest request) {
        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found: " + authenticatedUserId));

        boolean ok = passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash());
        if (!ok) {
            throw new UnauthorizedException("INVALID_CURRENT_PASSWORD", "Current password is incorrect");
        }

        String newHash = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(newHash);
        userRepository.save(user);
    }
}
