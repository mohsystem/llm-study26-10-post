package com.um.springbootprojstructure.config;

import com.um.springbootprojstructure.service.SessionTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SessionTokenService sessionTokenService) throws Exception {
        http
            .csrf(org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // for H2 console
            .addFilterBefore(new SessionTokenAuthFilter(sessionTokenService), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/register").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/reset-request").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/reset-confirm").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/mfa/challenge").authenticated()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/mfa/verify").authenticated()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/change-password").hasAnyRole("USER", "ADMIN")
                    .anyRequest().hasAnyRole("USER", "ADMIN")
            );

        // We don't need httpBasic anymore; token will be used.
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
