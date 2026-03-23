package com.um.springbootprojstructure.service.exception;

public class DuplicateAccountException extends ApiException {
    public DuplicateAccountException(String reasonCode, String message) {
        super(reasonCode, message);
    }
}
