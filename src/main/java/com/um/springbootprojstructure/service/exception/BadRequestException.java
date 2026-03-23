package com.um.springbootprojstructure.service.exception;

public class BadRequestException extends ApiException {
    public BadRequestException(String reasonCode, String message) {
        super(reasonCode, message);
    }
}
