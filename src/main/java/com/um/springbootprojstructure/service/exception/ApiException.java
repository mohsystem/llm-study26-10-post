package com.um.springbootprojstructure.service.exception;

public abstract class ApiException extends RuntimeException {
    private final String reasonCode;

    protected ApiException(String reasonCode, String message) {
        super(message);
        this.reasonCode = reasonCode;
    }

    public String getReasonCode() { return reasonCode; }
}
