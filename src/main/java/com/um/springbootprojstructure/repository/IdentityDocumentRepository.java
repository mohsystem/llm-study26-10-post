package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.IdentityDocument;
import com.um.springbootprojstructure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument, Long> {
    Optional<IdentityDocument> findByUser(User user);
    Optional<IdentityDocument> findByUser_PublicRef(String publicRef);
}
