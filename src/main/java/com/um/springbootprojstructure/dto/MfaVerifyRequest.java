package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MfaVerifyRequest {

    @NotBlank
    @Size(min = 4, max = 10)
    private String code;

    public MfaVerifyRequest() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
