package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.ApiKey;
import com.um.springbootprojstructure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    List<ApiKey> findByOwnerOrderByIdDesc(User owner);
    Optional<ApiKey> findByIdAndOwner(Long id, User owner);
}
