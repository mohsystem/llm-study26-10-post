package com.um.springbootprojstructure.service;

import jakarta.servlet.http.HttpServletRequest;

public interface SessionContextService {
    String extractBearerToken(HttpServletRequest request);
}
