package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.dto.ApiKeyIssueRequest;
import com.um.springbootprojstructure.dto.ApiKeyIssueResponse;
import com.um.springbootprojstructure.dto.ApiKeyResponse;
import com.um.springbootprojstructure.entity.ApiKey;
import com.um.springbootprojstructure.entity.ApiKeyStatus;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.ApiKeyRepository;
import com.um.springbootprojstructure.repository.UserRepository;
import com.um.springbootprojstructure.service.ApiKeyService;
import com.um.springbootprojstructure.service.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.apiKeyRepository = apiKeyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiKeyIssueResponse issueKey(Long authenticatedUserId, ApiKeyIssueRequest request) {
        User owner = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found"));

        // Generate high-entropy key; return plaintext once.
        String plain = generateApiKey();

        // Store only hash
        String hash = passwordEncoder.encode(plain);

        ApiKey apiKey = new ApiKey(owner, request.getName(), hash);
        ApiKey saved = apiKeyRepository.save(apiKey);

        // Return the key ONCE.
        return new ApiKeyIssueResponse(saved.getId(), saved.getName(), saved.getStatus(), saved.getCreatedAt(), plain);
    }

    @Override
    public List<ApiKeyResponse> listKeys(Long authenticatedUserId) {
        User owner = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found"));

        return apiKeyRepository.findByOwnerOrderByIdDesc(owner)
                .stream()
                .map(k -> new ApiKeyResponse(
                        k.getId(),
                        k.getName(),
                        k.getStatus(),
                        k.getCreatedAt(),
                        k.getRevokedAt(),
                        k.getKeyHash().substring(0, Math.min(8, k.getKeyHash().length()))
                ))
                .toList();
    }

    @Override
    public void revokeKey(Long authenticatedUserId, Long keyId) {
        User owner = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found"));

        ApiKey key = apiKeyRepository.findByIdAndOwner(keyId, owner)
                .orElseThrow(() -> new NotFoundException("API_KEY_NOT_FOUND", "API key not found"));

        if (key.getStatus() != ApiKeyStatus.REVOKED) {
            key.setStatus(ApiKeyStatus.REVOKED);
            key.setRevokedAt(Instant.now());
            apiKeyRepository.save(key);
        }
    }

    private String generateApiKey() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
