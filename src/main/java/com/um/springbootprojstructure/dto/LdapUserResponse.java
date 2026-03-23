package com.um.springbootprojstructure.dto;

import java.util.Map;

public class LdapUserResponse {
    private String dn;
    private Map<String, Object> attributes;

    public LdapUserResponse() {}

    public LdapUserResponse(String dn, Map<String, Object> attributes) {
        this.dn = dn;
        this.attributes = attributes;
    }

    public String getDn() { return dn; }
    public void setDn(String dn) { this.dn = dn; }

    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
}
