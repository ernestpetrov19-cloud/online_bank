package com.example.online_bank.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String json = """
                {
                "error": "%s",
                "message": "%s",
                "path": "%s"
                }
                """.formatted("UNAUTHORIZED", authException.getMessage(), request.getServletPath());
        response.getWriter().write(json);
    }
}
