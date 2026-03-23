package com.um.springbootprojstructure.dto;

import com.um.springbootprojstructure.entity.ApiKeyStatus;

import java.time.Instant;

public class ApiKeyResponse {
    private Long keyId;
    private String name;
    private ApiKeyStatus status;
    private Instant createdAt;
    private Instant revokedAt;

    // small hint for humans; not the key itself
    private String keyPrefix;

    public ApiKeyResponse() {}

    public ApiKeyResponse(Long keyId, String name, ApiKeyStatus status, Instant createdAt, Instant revokedAt, String keyPrefix) {
        this.keyId = keyId;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.revokedAt = revokedAt;
        this.keyPrefix = keyPrefix;
    }

    public Long getKeyId() { return keyId; }
    public void setKeyId(Long keyId) { this.keyId = keyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ApiKeyStatus getStatus() { return status; }
    public void setStatus(ApiKeyStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }

    public String getKeyPrefix() { return keyPrefix; }
    public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }
}
