package com.ecomptaia.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Point d'entrée pour l'authentification JWT
 * Gère les erreurs d'authentification non autorisées
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String errorMessage = "{\"error\":\"Accès non autorisé\",\"message\":\"" + 
                            authException.getMessage() + "\",\"path\":\"" + 
                            request.getRequestURI() + "\"}";
        
        response.getWriter().write(errorMessage);
    }
}
