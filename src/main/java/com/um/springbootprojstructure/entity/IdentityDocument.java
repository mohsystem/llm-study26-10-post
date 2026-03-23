package com.um.springbootprojstructure.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "identity_documents",
       indexes = @Index(name = "idx_identity_documents_user_id", columnList = "user_id", unique = true))
public class IdentityDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 120)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] content;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @PrePersist
    void onUpload() {
        this.uploadedAt = Instant.now();
    }

    public IdentityDocument() {}

    public IdentityDocument(User user, String fileName, String contentType, long sizeBytes, byte[] content) {
        this.user = user;
        this.fileName = fileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.content = content;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public byte[] getContent() { return content; }
    public Instant getUploadedAt() { return uploadedAt; }

    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
    public void setContent(byte[] content) { this.content = content; }
}
