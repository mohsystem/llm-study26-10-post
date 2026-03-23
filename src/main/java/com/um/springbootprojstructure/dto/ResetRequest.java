package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetRequest {

    @NotBlank
    @Size(min = 3, max = 160)
    private String identifier; // username OR email

    public ResetRequest() {}

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
}
