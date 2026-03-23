package com.um.springbootprojstructure.dto;

public class RegisterResponse {

    private Long accountId;
    private String status;

    public RegisterResponse() {}

    public RegisterResponse(Long accountId, String status) {
        this.accountId = accountId;
        this.status = status;
    }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
