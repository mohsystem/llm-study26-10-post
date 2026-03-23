package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "api_keys", indexes = {
        @Index(name = "idx_api_keys_key_hash", columnList = "keyHash", unique = true),
        @Index(name = "idx_api_keys_owner_user_id", columnList = "owner_user_id"),
        @Index(name = "idx_api_keys_status", columnList = "status")
})
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Can represent a user or service account. For now, use User.
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 120)
    private String name; // display label like "Partner X integration"

    @Column(nullable = false, unique = true, length = 120)
    private String keyHash; // store only hash

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ApiKeyStatus status = ApiKeyStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant revokedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public ApiKey() {}

    public ApiKey(User owner, String name, String keyHash) {
        this.owner = owner;
        this.name = name;
        this.keyHash = keyHash;
        this.status = ApiKeyStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public User getOwner() { return owner; }
    public String getName() { return name; }
    public String getKeyHash() { return keyHash; }

    public ApiKeyStatus getStatus() { return status; }
    public void setStatus(ApiKeyStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
}
