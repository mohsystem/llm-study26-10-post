package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.ChangePasswordRequest;
import com.um.springbootprojstructure.dto.LoginRequest;
import com.um.springbootprojstructure.dto.RegisterRequest;
import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.entity.User;

public interface AuthService {
    User register(RegisterRequest request);
    SessionToken login(LoginRequest request);

    void changePassword(Long authenticatedUserId, ChangePasswordRequest request);
}
