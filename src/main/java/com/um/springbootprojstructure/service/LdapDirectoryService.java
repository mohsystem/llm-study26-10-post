package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.dto.LdapUserResponse;

import java.util.List;

public interface LdapDirectoryService {
    List<LdapUserResponse> searchUser(String dc, String username);
}
