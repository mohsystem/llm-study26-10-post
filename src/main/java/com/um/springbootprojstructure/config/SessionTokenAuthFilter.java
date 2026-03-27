package com.um.springbootprojstructure.config;

import com.um.springbootprojstructure.entity.SessionToken;
import com.um.springbootprojstructure.entity.User;
import com.um.springbootprojstructure.service.SessionTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class SessionTokenAuthFilter extends OncePerRequestFilter {

    private final SessionTokenService sessionTokenService;

    public SessionTokenAuthFilter(SessionTokenService sessionTokenService) {
        this.sessionTokenService = sessionTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length()).trim();

            sessionTokenService.findValid(token).ifPresent(session -> {
                User user = session.getUser();

                // Authority style: ROLE_USER / ROLE_ADMIN
                String roleName = session.isMfaVerified() ? "ROLE_" + user.getRole().name() : "ROLE_PRE_AUTH";
                var auth = new UsernamePasswordAuthenticationToken(
                        user.getId(), // principal
                        null,
                        List.of(new SimpleGrantedAuthority(roleName))
                );
                auth.setDetails(user);
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }

        filterChain.doFilter(request, response);
    }
}
