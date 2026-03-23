package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.Role;
import com.um.springbootprojstructure.entity.User;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private UserSpecifications() {}

    public static Specification<User> hasRole(Role role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<User> hasStatus(AccountStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
