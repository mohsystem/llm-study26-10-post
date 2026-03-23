package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.Role;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.repository.UserRepository;
import com.um.springbootprojstructure.repository.UserSpecifications;
import com.um.springbootprojstructure.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> listUsers(int page, int size, Optional<Role> role, Optional<AccountStatus> status) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(size, 1), 200);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));

        Specification<User> spec = Specification.where(null);
        if (role.isPresent()) {
            spec = spec.and(UserSpecifications.hasRole(role.get()));
        }
        if (status.isPresent()) {
            spec = spec.and(UserSpecifications.hasStatus(status.get()));
        }

        return userRepository.findAll(spec, pageable);
    }
}
