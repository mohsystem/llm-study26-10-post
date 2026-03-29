package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.ApiKeyIssueRequest;
import com.um.springbootprojstructure.dto.ApiKeyIssueResponse;
import com.um.springbootprojstructure.dto.ApiKeyResponse;
import com.um.springbootprojstructure.dto.ChangePasswordRequest;
import com.um.springbootprojstructure.dto.LoginRequest;
import com.um.springbootprojstructure.dto.LoginResponse;
import com.um.springbootprojstructure.dto.MfaChallengeResponse;
import com.um.springbootprojstructure.dto.MfaVerifyRequest;
import com.um.springbootprojstructure.dto.MfaVerifyResponse;
import com.um.springbootprojstructure.dto.RegisterRequest;
import com.um.springbootprojstructure.dto.RegisterResponse;
import com.um.springbootprojstructure.dto.ResetConfirmRequest;
import com.um.springbootprojstructure.dto.ResetRequest;
import com.um.springbootprojstructure.dto.StatusResponse;
import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.service.ApiKeyService;
import com.um.springbootprojstructure.service.AuthService;
import com.um.springbootprojstructure.service.MfaService;
import com.um.springbootprojstructure.service.PasswordResetService;
import com.um.springbootprojstructure.service.SessionContextService;
import com.um.springbootprojstructure.service.SessionTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger promptLogger = LoggerFactory.getLogger("USER_PROMPT_LOGGER");

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final SessionContextService sessionContextService;
    private final SessionTokenService sessionTokenService;
    private final MfaService mfaService;
    private final ApiKeyService apiKeyService;

    public AuthController(AuthService authService,
                          PasswordResetService passwordResetService,
                          SessionContextService sessionContextService,
                          SessionTokenService sessionTokenService,
                          MfaService mfaService,
                          ApiKeyService apiKeyService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
        this.sessionContextService = sessionContextService;
        this.sessionTokenService = sessionTokenService;
        this.mfaService = mfaService;
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        String safeUsername = request.getUsername().replaceAll("[\r\n]", "_");
        String safeEmail = request.getEmail().replaceAll("[\r\n]", "_");
        promptLogger.info("Register request: username={}, email={}", safeUsername, safeEmail);
        User created = authService.register(request);
        RegisterResponse response = new RegisterResponse(created.getId(), "ACCOUNT_CREATED");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }




    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        promptLogger.info("Login request: identifier={}", request.getIdentifier());
        SessionToken issued = authService.login(request);

        long expiresInSeconds = issued.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();
        if (expiresInSeconds < 0) {
            expiresInSeconds = 0;
        }

        LoginResponse response = new LoginResponse(issued.getToken(), expiresInSeconds);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa/challenge")
    public ResponseEntity<MfaChallengeResponse> mfaChallenge(HttpServletRequest request) {
        String token = sessionContextService.extractBearerToken(request);
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Missing Bearer token");
        }

        SessionToken sessionToken = sessionTokenService.findValid(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired session token"));

        String status = mfaService.challenge(sessionToken);
        return ResponseEntity.ok(new MfaChallengeResponse(status));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<MfaVerifyResponse> mfaVerify(@Valid @RequestBody MfaVerifyRequest request,
                                                       HttpServletRequest httpRequest) {
        String token = sessionContextService.extractBearerToken(httpRequest);
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Missing Bearer token");
        }

        SessionToken sessionToken = sessionTokenService.findValid(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired session token"));

        String status = mfaService.verify(sessionToken, request.getCode());
        return ResponseEntity.ok(new MfaVerifyResponse(status));
    }

    @PostMapping("/api-keys")
    public ResponseEntity<ApiKeyIssueResponse> issueApiKey(@Valid @RequestBody ApiKeyIssueRequest request,
                                                           Authentication authentication) {
        Long authenticatedUserId = (Long) authentication.getPrincipal();
        promptLogger.info("POST /api/auth/api-keys userId={}, name={}", authenticatedUserId, request.getName());

        ApiKeyIssueResponse response = apiKeyService.issueKey(authenticatedUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api-keys")
    public ResponseEntity<List<ApiKeyResponse>> listApiKeys(Authentication authentication) {
        Long authenticatedUserId = (Long) authentication.getPrincipal();
        promptLogger.info("GET /api/auth/api-keys userId={}", authenticatedUserId);

        List<ApiKeyResponse> response = apiKeyService.listKeys(authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api-keys/{keyId}")
    public ResponseEntity<StatusResponse> revokeApiKey(@PathVariable Long keyId,
                                                       Authentication authentication) {
        Long authenticatedUserId = (Long) authentication.getPrincipal();
        promptLogger.info("DELETE /api/auth/api-keys/{} userId={}", keyId, authenticatedUserId);

        apiKeyService.revokeKey(authenticatedUserId, keyId);
        return ResponseEntity.ok(new StatusResponse("API_KEY_REVOKED"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<StatusResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                         Authentication authentication) {
        Long authenticatedUserId = (Long) authentication.getPrincipal();
        promptLogger.info("Change-password request: userId={}", authenticatedUserId);

        authService.changePassword(authenticatedUserId, request);
        return ResponseEntity.ok(new StatusResponse("PASSWORD_CHANGED"));
    }

    @PostMapping("/reset-request")
    public ResponseEntity<StatusResponse> resetRequest(@Valid @RequestBody ResetRequest request) {
        // Do not leak if user exists; do not log sensitive details beyond identifier
        promptLogger.info("POST /api/auth/reset-request identifier={}", request.getIdentifier());

        String status = passwordResetService.initiateReset(request.getIdentifier());

        // For testing visibility (since no email), you might want to log token value.
        // We are NOT returning the token in API response per typical flows.
        return ResponseEntity.ok(new StatusResponse(status));
    }

    @PostMapping("/reset-confirm")
    public ResponseEntity<StatusResponse> resetConfirm(@Valid @RequestBody ResetConfirmRequest request) {
        // Do not log password. Token is also sensitive; log only presence/short prefix.
        String tokenPreview = request.getToken().length() >= 6 ? request.getToken().substring(0, 6) : "short";
        promptLogger.info("POST /api/auth/reset-confirm tokenPrefix={}", tokenPreview);

        String status = passwordResetService.confirmReset(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new StatusResponse(status));
    }
}
