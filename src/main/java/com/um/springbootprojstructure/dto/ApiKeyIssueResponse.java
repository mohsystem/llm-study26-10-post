package com.um.springbootprojstructure.dto;

import com.um.springbootprojstructure.entity.ApiKeyStatus;

import java.time.Instant;

public class ApiKeyIssueResponse {
    private Long keyId;
    private String name;
    private ApiKeyStatus status;
    private Instant createdAt;

    /**
     * Returned only once on creation.
     */
    private String apiKey;

    public ApiKeyIssueResponse() {}

    public ApiKeyIssueResponse(Long keyId, String name, ApiKeyStatus status, Instant createdAt, String apiKey) {
        this.keyId = keyId;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.apiKey = apiKey;
    }

    public Long getKeyId() { return keyId; }
    public void setKeyId(Long keyId) { this.keyId = keyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ApiKeyStatus getStatus() { return status; }
    public void setStatus(ApiKeyStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
