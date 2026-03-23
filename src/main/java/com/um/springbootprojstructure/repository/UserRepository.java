package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByPublicRef(String publicRef);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
