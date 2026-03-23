package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ApiKeyIssueRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    private String name;

    public ApiKeyIssueRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
