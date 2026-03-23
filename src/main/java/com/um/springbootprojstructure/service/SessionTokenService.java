package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.entity.User;

import java.util.Optional;

public interface SessionTokenService {
    SessionToken issueToken(User user);
    Optional<SessionToken> findValid(String token);
}
