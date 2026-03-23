package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.entity.MfaOtp;
import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.MfaOtpRepository;
import com.um.springbootprojstructure.repository.SessionTokenRepository;
import com.um.springbootprojstructure.service.MfaService;
import com.um.springbootprojstructure.service.NotificationGatewayService;
import com.um.springbootprojstructure.service.exception.BadRequestException;
import com.um.springbootprojstructure.service.exception.NotFoundException;
import com.um.springbootprojstructure.service.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
@Transactional
public class MfaServiceImpl implements MfaService {

    private final MfaOtpRepository mfaOtpRepository;
    private final SessionTokenRepository sessionTokenRepository;
    private final NotificationGatewayService notificationGatewayService;
    private final PasswordEncoder passwordEncoder;
    private final long ttlSeconds;
    private final int maxAttempts;
    private final int codeLength;

    public MfaServiceImpl(MfaOtpRepository mfaOtpRepository,
                          SessionTokenRepository sessionTokenRepository,
                          NotificationGatewayService notificationGatewayService,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.mfa.otp-ttl-seconds:300}") long ttlSeconds,
                          @Value("${app.mfa.max-attempts:5}") int maxAttempts,
                          @Value("${app.mfa.code-length:6}") int codeLength) {
        this.mfaOtpRepository = mfaOtpRepository;
        this.sessionTokenRepository = sessionTokenRepository;
        this.notificationGatewayService = notificationGatewayService;
        this.passwordEncoder = passwordEncoder;
        this.ttlSeconds = ttlSeconds;
        this.maxAttempts = maxAttempts;
        this.codeLength = codeLength;
    }

    @Override
    public String challenge(SessionToken sessionToken) {
        mfaOtpRepository.deleteByExpiresAtBefore(Instant.now());

        SessionToken managedSession = sessionTokenRepository.findByToken(sessionToken.getToken())
                .orElseThrow(() -> new UnauthorizedException("INVALID_SESSION_TOKEN", "Invalid session token"));

        User user = managedSession.getUser();
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            throw new BadRequestException("PHONE_NUMBER_REQUIRED", "Phone number is required for MFA challenge");
        }

        String code = generateNumericCode(codeLength);
        String codeHash = passwordEncoder.encode(code);
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);

        mfaOtpRepository.save(new MfaOtp(managedSession, codeHash, expiresAt));

        notificationGatewayService.sendSms(user.getPhoneNumber(), "Your verification code is: " + code);

        return "MFA_CHALLENGE_SENT";
    }

    @Override
    public String verify(SessionToken sessionToken, String code) {
        mfaOtpRepository.deleteByExpiresAtBefore(Instant.now());

        SessionToken managedSession = sessionTokenRepository.findByToken(sessionToken.getToken())
                .orElseThrow(() -> new UnauthorizedException("INVALID_SESSION_TOKEN", "Invalid session token"));

        MfaOtp otp = mfaOtpRepository.findTopBySessionToken_TokenOrderByCreatedAtDesc(managedSession.getToken())
                .orElseThrow(() -> new NotFoundException("MFA_CHALLENGE_NOT_FOUND", "MFA challenge not found"));

        if (otp.isUsed()) {
            throw new BadRequestException("OTP_ALREADY_USED", "OTP already used");
        }
        if (!otp.getExpiresAt().isAfter(Instant.now())) {
            throw new UnauthorizedException("OTP_EXPIRED", "OTP expired");
        }
        if (otp.getAttempts() >= maxAttempts) {
            throw new UnauthorizedException("OTP_ATTEMPTS_EXCEEDED", "OTP attempts exceeded");
        }

        if (!passwordEncoder.matches(code, otp.getCodeHash())) {
            otp.setAttempts(otp.getAttempts() + 1);
            mfaOtpRepository.save(otp);
            throw new UnauthorizedException("INVALID_OTP", "Invalid OTP");
        }

        otp.setUsed(true);
        mfaOtpRepository.save(otp);

        managedSession.setMfaVerified(true);
        sessionTokenRepository.save(managedSession);

        return "MFA_VERIFIED";
    }

    private String generateNumericCode(int digits) {
        int safeDigits = Math.max(4, Math.min(digits, 10));
        int bound = (int) Math.pow(10, safeDigits);
        int min = (int) Math.pow(10, safeDigits - 1);
        int value = new Random().nextInt(bound - min) + min;
        return String.valueOf(value);
    }
}
