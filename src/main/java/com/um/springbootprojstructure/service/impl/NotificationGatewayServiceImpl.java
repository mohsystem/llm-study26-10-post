package com.um.springbootprojstructure.service.impl;

import com.um.springbootprojstructure.dto.SmsSendRequest;
import com.um.springbootprojstructure.service.NotificationGatewayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationGatewayServiceImpl implements NotificationGatewayService {

    private final RestClient restClient;
    private final String baseUrl;
    private final String smsPath;

    public NotificationGatewayServiceImpl(RestClient restClient,
                                          @Value("${app.notifications.base-url}") String baseUrl,
                                          @Value("${app.notifications.sms-path:/sms/send}") String smsPath) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
        this.smsPath = smsPath;
    }

    @Override
    public void sendSms(String toPhoneNumber, String message) {
        restClient.post()
                .uri(baseUrl + smsPath)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SmsSendRequest(toPhoneNumber, message))
                .retrieve()
                .toBodilessEntity();
    }
}
