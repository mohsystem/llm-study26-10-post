package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.service.SessionContextService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class SessionContextServiceImpl implements SessionContextService {
    @Override
    public String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        return header.substring("Bearer ".length()).trim();
    }
}
