package com.um.springbootprojstructure.service;

import com.um.springbootprojstructure.entity.IdentityDocument;
import org.springframework.web.multipart.MultipartFile;

public interface IdentityDocumentService {
    IdentityDocument getByUserPublicRef(String publicRef);

    void upsertForUserPublicRef(String publicRef, MultipartFile file);
}
