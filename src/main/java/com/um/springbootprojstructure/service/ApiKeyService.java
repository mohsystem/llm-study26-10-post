package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ApiKeyIssueRequest;
import com.um.springbootprojstructure.dto.ApiKeyIssueResponse;
import com.um.springbootprojstructure.dto.ApiKeyResponse;

import java.util.List;

public interface ApiKeyService {
    ApiKeyIssueResponse issueKey(Long authenticatedUserId, ApiKeyIssueRequest request);
    List<ApiKeyResponse> listKeys(Long authenticatedUserId);
    void revokeKey(Long authenticatedUserId, Long keyId);
}
