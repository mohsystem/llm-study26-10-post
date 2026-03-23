package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.Role;
import com.um.springbootprojstructure.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {
    // existing methods omitted for brevity (keep them if you still use them)

    Page<User> listUsers(int page, int size, Optional<Role> role, Optional<AccountStatus> status);
}
