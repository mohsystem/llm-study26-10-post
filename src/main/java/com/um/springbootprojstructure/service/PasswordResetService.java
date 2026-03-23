package com.um.springbootprojstructure.service;

public interface PasswordResetService {
    String initiateReset(String identifier);

    /**
     * Confirm reset token and set new password.
     */
    String confirmReset(String token, String newPassword);
}
