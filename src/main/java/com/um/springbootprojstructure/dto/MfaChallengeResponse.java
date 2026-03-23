package com.um.springbootprojstructure.dto;

public class MfaChallengeResponse {
    private String status;

    public MfaChallengeResponse() {}
    public MfaChallengeResponse(String status) { this.status = status; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
