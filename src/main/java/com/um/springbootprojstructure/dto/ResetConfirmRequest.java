package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetConfirmRequest {

    @NotBlank
    @Size(min = 16, max = 64)
    private String token;

    @NotBlank
    @Size(min = 8, max = 72)
    private String newPassword;

    public ResetConfirmRequest() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
