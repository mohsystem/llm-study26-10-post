package com.um.springbootprojstructure.dto;

import com.um.springbootprojstructure.entity.AccountStatus;
import com.um.springbootprojstructure.entity.Role;

import java.time.Instant;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private Instant createdAt;

    public UserResponse() {}

    public UserResponse(Long id, String username, String email, Role role, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
