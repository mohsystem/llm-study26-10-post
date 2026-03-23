package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "session_tokens", indexes = {
        @Index(name = "idx_session_tokens_token", columnList = "token", unique = true),
        @Index(name = "idx_session_tokens_user_id", columnList = "user_id")
})
public class SessionToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean mfaVerified = false;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public SessionToken() {}

    public SessionToken(String token, User user, Instant expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public User getUser() { return user; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }

    public boolean isMfaVerified() { return mfaVerified; }
    public void setMfaVerified(boolean mfaVerified) { this.mfaVerified = mfaVerified; }

    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
