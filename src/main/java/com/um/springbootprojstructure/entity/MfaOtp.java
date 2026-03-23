package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "mfa_otps", indexes = {
        @Index(name = "idx_mfa_otps_session_id", columnList = "session_token_id"),
        @Index(name = "idx_mfa_otps_expires", columnList = "expiresAt")
})
public class MfaOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_token_id", nullable = false)
    private SessionToken sessionToken;

    @Column(nullable = false, length = 100)
    private String codeHash;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public MfaOtp() {}

    public MfaOtp(SessionToken sessionToken, String codeHash, Instant expiresAt) {
        this.sessionToken = sessionToken;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public SessionToken getSessionToken() { return sessionToken; }
    public String getCodeHash() { return codeHash; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
