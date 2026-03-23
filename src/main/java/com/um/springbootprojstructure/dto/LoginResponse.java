package com.um.springbootprojstructure.dto;

public class LoginResponse {

    private String token;
    private String tokenType = "SESSION";
    private Long expiresInSeconds;

    public LoginResponse() {}

    public LoginResponse(String token, Long expiresInSeconds) {
        this.token = token;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(Long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }
}
