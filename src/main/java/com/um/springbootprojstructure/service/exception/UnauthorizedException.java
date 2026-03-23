package com.um.springbootprojstructure.service.exception;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String reasonCode, String message) {
        super(reasonCode, message);
    }
}
