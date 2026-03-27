package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.StatusResponse;
import com.um.springbootprojstructure.dto.UserResponse;
import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.IdentityDocument;
import com.um.springbootprojstructure.entity.Role;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.mapper.UserMapper;
import com.um.springbootprojstructure.service.IdentityDocumentService;
import com.um.springbootprojstructure.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger promptLogger = LoggerFactory.getLogger("USER_PROMPT_LOGGER");

    private final UserService userService;
    private final IdentityDocumentService identityDocumentService;

    public UserController(UserService userService, IdentityDocumentService identityDocumentService) {
        this.userService = userService;
        this.identityDocumentService = identityDocumentService;
    }

    @GetMapping
    public Page<UserResponse> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) AccountStatus status,
            Authentication authentication
    ) {
        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("FORBIDDEN");
        }
        promptLogger.info("GET /api/users page={}, size={}, role={}, status={}", page, size, role, status);
        Page<User> users = userService.listUsers(page, size, Optional.ofNullable(role), Optional.ofNullable(status));
        return users.map(UserMapper::toResponse);
    }

    @GetMapping("/{publicRef}/document")
    public ResponseEntity<byte[]> getIdentityDocument(@PathVariable String publicRef, Authentication authentication) {
        User authenticatedUser = (User) authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !authenticatedUser.getPublicRef().equals(publicRef)) {
            throw new AccessDeniedException("FORBIDDEN");
        }
        promptLogger.info("GET /api/users/{}/document", publicRef);

        IdentityDocument doc = identityDocumentService.getByUserPublicRef(publicRef);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(doc.getContentType()));
        headers.setContentLength(doc.getSizeBytes());
        headers.setContentDisposition(ContentDisposition.attachment().filename(doc.getFileName()).build());

        return new ResponseEntity<>(doc.getContent(), headers, HttpStatus.OK);
    }

    @PutMapping(value = "/{publicRef}/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StatusResponse> uploadOrReplaceIdentityDocument(
            @PathVariable String publicRef,
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {
        User authenticatedUser = (User) authentication.getDetails();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !authenticatedUser.getPublicRef().equals(publicRef)) {
            throw new AccessDeniedException("FORBIDDEN");
        }
        promptLogger.info("PUT /api/users/{}/document fileName={}, size={}, contentType={}",
                publicRef,
                file != null ? file.getOriginalFilename() : null,
                file != null ? file.getSize() : null,
                file != null ? file.getContentType() : null
        );

        identityDocumentService.upsertForUserPublicRef(publicRef, file);
        return ResponseEntity.ok(new StatusResponse("DOCUMENT_UPDATED"));
    }
}
