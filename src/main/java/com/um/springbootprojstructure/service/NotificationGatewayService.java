package com.um.springbootprojstructure.service;

public interface NotificationGatewayService {
    void sendSms(String toPhoneNumber, String message);
}
