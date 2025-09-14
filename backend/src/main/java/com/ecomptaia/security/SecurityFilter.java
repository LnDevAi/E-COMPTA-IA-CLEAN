package com.ecomptaia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Applique le rate limiting et d'autres mesures de sécurité
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitingService rateLimitingService;

    @Autowired
    private SecurityValidationService securityValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);

        // Ajouter les headers de sécurité
        addSecurityHeaders(response);

        // Vérifier le rate limiting
        if (!rateLimitingService.isRequestAllowed(clientIp)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-RateLimit-Limit", String.valueOf(100));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitingService.getResetTime(clientIp)));
            response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
            return;
        }

        // Ajouter les headers de rate limiting
        response.setHeader("X-RateLimit-Limit", "100");
        response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitingService.getRemainingRequests(clientIp)));
        response.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitingService.getResetTime(clientIp)));

        // Vérifier les paramètres de requête pour les injections
        if (hasDangerousParameters(request)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("{\"error\":\"Invalid request parameters\"}");
            return;
        }

        // Continuer avec la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    /**
     * Obtient l'adresse IP réelle du client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Ajoute les headers de sécurité
     */
    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Content-Security-Policy", 
            "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:; connect-src 'self' ws: wss:;");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
        response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
        response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
        response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
    }

    /**
     * Vérifie si la requête contient des paramètres dangereux
     */
    private boolean hasDangerousParameters(HttpServletRequest request) {
        // Vérifier les paramètres de requête
        request.getParameterMap().forEach((key, values) -> {
            for (String value : values) {
                if (!securityValidationService.isSafeForSQL(value) || 
                    !securityValidationService.isSafeForXSS(value)) {
                    throw new SecurityException("Dangerous parameter detected");
                }
            }
        });

        // Vérifier les headers
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            String headerValue = request.getHeader(headerName);
            if (!securityValidationService.isSafeForSQL(headerValue) || 
                !securityValidationService.isSafeForXSS(headerValue)) {
                throw new SecurityException("Dangerous header detected");
            }
        });

        return false;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Ne pas appliquer le filtre sur les endpoints de santé
        return path.startsWith("/actuator/health") || path.startsWith("/health");
    }
}
