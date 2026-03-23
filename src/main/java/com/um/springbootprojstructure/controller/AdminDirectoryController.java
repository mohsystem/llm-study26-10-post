package com.um.springbootprojstructure.controller;

import com.um.springbootprojstructure.dto.LdapUserResponse;
import com.um.springbootprojstructure.dto.OperationResponse;
import com.um.springbootprojstructure.service.LdapDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/directory")
public class AdminDirectoryController {

    private static final Logger promptLogger = LoggerFactory.getLogger("USER_PROMPT_LOGGER");

    private final LdapDirectoryService ldapDirectoryService;

    public AdminDirectoryController(LdapDirectoryService ldapDirectoryService) {
        this.ldapDirectoryService = ldapDirectoryService;
    }

    @GetMapping("/user-search")
    public ResponseEntity<OperationResponse> userSearch(
            @RequestParam String dc,
            @RequestParam String username,
            Authentication authentication
    ) {
        // auth principal is userId. (role enforcement configured below)
        promptLogger.info("GET /api/admin/directory/user-search dc={} username={}", dc, username);

        List<LdapUserResponse> results = ldapDirectoryService.searchUser(dc, username);

        return ResponseEntity.ok(OperationResponse.accepted(
                "/api/admin/directory/user-search",
                "LDAP_SEARCH_OK",
                Map.of("results", results, "count", results.size())
        ));
    }
}
