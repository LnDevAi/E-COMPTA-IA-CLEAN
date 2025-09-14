package com.ecomptaia.security;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service de surveillance de la sécurité pour E-COMPTA-IA INTERNATIONAL
 * Collecte les métriques de sécurité pour Prometheus
 */
@Service
public class SecurityMonitoringService {


    // Compteurs de sécurité
    private final AtomicLong authenticationFailures = new AtomicLong(0);
    private final AtomicLong authorizationFailures = new AtomicLong(0);
    private final AtomicLong rateLimitHits = new AtomicLong(0);
    private final AtomicLong suspiciousRequests = new AtomicLong(0);
    private final AtomicLong sqlInjectionAttempts = new AtomicLong(0);
    private final AtomicLong xssAttempts = new AtomicLong(0);

    /**
     * Enregistre une tentative d'authentification échouée
     */
    public void recordAuthenticationFailure(String username, String reason) {
        authenticationFailures.incrementAndGet();
        logSecurityEvent("AUTHENTICATION_FAILURE", username, reason);
    }

    /**
     * Enregistre une tentative d'autorisation échouée
     */
    public void recordAuthorizationFailure(String username, String resource) {
        authorizationFailures.incrementAndGet();
        logSecurityEvent("AUTHORIZATION_FAILURE", username, resource);
    }

    /**
     * Enregistre un hit de rate limiting
     */
    public void recordRateLimitHit(String clientIp) {
        rateLimitHits.incrementAndGet();
        logSecurityEvent("RATE_LIMIT_HIT", clientIp, "Rate limit exceeded");
    }

    /**
     * Enregistre une requête suspecte
     */
    public void recordSuspiciousRequest(String clientIp, String reason) {
        suspiciousRequests.incrementAndGet();
        logSecurityEvent("SUSPICIOUS_REQUEST", clientIp, reason);
    }

    /**
     * Enregistre une tentative d'injection SQL
     */
    public void recordSqlInjectionAttempt(String clientIp, String input) {
        sqlInjectionAttempts.incrementAndGet();
        logSecurityEvent("SQL_INJECTION_ATTEMPT", clientIp, input);
    }

    /**
     * Enregistre une tentative XSS
     */
    public void recordXssAttempt(String clientIp, String input) {
        xssAttempts.incrementAndGet();
        logSecurityEvent("XSS_ATTEMPT", clientIp, input);
    }

    /**
     * Obtient le nombre d'échecs d'authentification
     */
    public long getAuthenticationFailures() {
        return authenticationFailures.get();
    }

    /**
     * Obtient le nombre d'échecs d'autorisation
     */
    public long getAuthorizationFailures() {
        return authorizationFailures.get();
    }

    /**
     * Obtient le nombre de hits de rate limiting
     */
    public long getRateLimitHits() {
        return rateLimitHits.get();
    }

    /**
     * Obtient le nombre de requêtes suspectes
     */
    public long getSuspiciousRequests() {
        return suspiciousRequests.get();
    }

    /**
     * Obtient le nombre de tentatives d'injection SQL
     */
    public long getSqlInjectionAttempts() {
        return sqlInjectionAttempts.get();
    }

    /**
     * Obtient le nombre de tentatives XSS
     */
    public long getXssAttempts() {
        return xssAttempts.get();
    }

    /**
     * Log un événement de sécurité
     */
    private void logSecurityEvent(String eventType, String clientIp, String details) {
        HttpServletRequest request = getCurrentRequest();
        String userAgent = request != null ? request.getHeader("User-Agent") : "Unknown";
        String requestUri = request != null ? request.getRequestURI() : "Unknown";
        
        // Log l'événement (à implémenter avec un logger approprié)
        System.out.println(String.format(
            "[SECURITY] %s - %s - %s - %s - %s - %s",
            LocalDateTime.now(),
            eventType,
            clientIp,
            userAgent,
            requestUri,
            details
        ));
    }

    /**
     * Obtient la requête courante
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
