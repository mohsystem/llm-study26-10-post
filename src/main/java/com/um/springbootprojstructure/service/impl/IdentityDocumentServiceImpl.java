package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.entity.IdentityDocument;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.IdentityDocumentRepository;
import com.um.springbootprojstructure.repository.UserRepository;
import com.um.springbootprojstructure.service.IdentityDocumentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
@Transactional
public class IdentityDocumentServiceImpl implements IdentityDocumentService {

    private final IdentityDocumentRepository identityDocumentRepository;
    private final UserRepository userRepository;

    private final long maxBytes;
    private final Set<String> allowedContentTypes;

    public IdentityDocumentServiceImpl(IdentityDocumentRepository identityDocumentRepository,
                                       UserRepository userRepository,
                                       @Value("${app.documents.max-bytes:5242880}") long maxBytes,
                                       @Value("${app.documents.allowed-content-types:application/pdf,image/jpeg,image/png}") String allowed) {
        this.identityDocumentRepository = identityDocumentRepository;
        this.userRepository = userRepository;
        this.maxBytes = maxBytes;
        this.allowedContentTypes = Set.of(allowed.split(","));
    }

    @Override
    public IdentityDocument getByUserPublicRef(String publicRef) {
        return identityDocumentRepository.findByUser_PublicRef(publicRef)
                .orElseThrow(() -> new IllegalArgumentException("Document not found for user publicRef=" + publicRef));
    }

    @Override
    public void upsertForUserPublicRef(String publicRef, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("File too large. Max bytes=" + maxBytes);
        }

        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        if (!allowedContentTypes.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }

        User user = userRepository.findByPublicRef(publicRef)
                .orElseThrow(() -> new IllegalArgumentException("User not found for publicRef=" + publicRef));

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read uploaded file");
        }

        String filename = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
                ? "identity-document"
                : file.getOriginalFilename();

        IdentityDocument doc = identityDocumentRepository.findByUser(user)
                .orElse(null);

        if (doc == null) {
            IdentityDocument created = new IdentityDocument(user, filename, contentType, bytes.length, bytes);
            identityDocumentRepository.save(created);
        } else {
            doc.setFileName(filename);
            doc.setContentType(contentType);
            doc.setSizeBytes(bytes.length);
            doc.setContent(bytes);
            identityDocumentRepository.save(doc);
        }
    }
}
