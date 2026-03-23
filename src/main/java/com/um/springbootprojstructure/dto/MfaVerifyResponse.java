package com.um.springbootprojstructure.dto;

public class MfaVerifyResponse {
    private String status;

    public MfaVerifyResponse() {}
    public MfaVerifyResponse(String status) { this.status = status; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
