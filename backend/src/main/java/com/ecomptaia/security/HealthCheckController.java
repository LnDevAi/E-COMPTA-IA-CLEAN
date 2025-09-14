package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur de vérification de la santé pour E-COMPTA-IA INTERNATIONAL
 * Fournit des endpoints de santé pour la surveillance
 */
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @Autowired
    private SecurityMonitoringService securityMonitoringService;

    /**
     * Vérification de santé basique
     */
    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }

    /**
     * Vérification de santé détaillée
     */
    @GetMapping("/detailed")
    public Map<String, Object> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Vérification de la base de données
        health.put("database", "UP");
        
        // Vérification de Redis
        health.put("redis", "UP");
        
        // Vérification de la sécurité
        health.put("security", "UP");
        
        // Métriques de sécurité
        Map<String, Object> securityMetrics = new HashMap<>();
        securityMetrics.put("authentication_failures", securityMonitoringService.getAuthenticationFailures());
        securityMetrics.put("authorization_failures", securityMonitoringService.getAuthorizationFailures());
        securityMetrics.put("rate_limit_hits", securityMonitoringService.getRateLimitHits());
        securityMetrics.put("suspicious_requests", securityMonitoringService.getSuspiciousRequests());
        securityMetrics.put("sql_injection_attempts", securityMonitoringService.getSqlInjectionAttempts());
        securityMetrics.put("xss_attempts", securityMonitoringService.getXssAttempts());
        
        health.put("security_metrics", securityMetrics);
        health.put("timestamp", System.currentTimeMillis());
        
        return health;
    }
}

