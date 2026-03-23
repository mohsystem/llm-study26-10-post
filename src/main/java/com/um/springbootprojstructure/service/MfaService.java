package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.entity.SessionToken;

public interface MfaService {
    String challenge(SessionToken sessionToken);
    String verify(SessionToken sessionToken, String code);
}
