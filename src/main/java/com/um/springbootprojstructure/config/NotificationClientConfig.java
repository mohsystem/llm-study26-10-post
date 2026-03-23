package com.um.springbootprojstructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class NotificationClientConfig {

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}
