package com.um.springbootprojstructure.service.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(String reasonCode, String message) {
        super(reasonCode, message);
    }
}
