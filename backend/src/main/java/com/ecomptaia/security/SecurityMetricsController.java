package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur des métriques de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Expose les métriques de sécurité pour Prometheus
 */
@RestController
@RequestMapping("/api/security/metrics")
public class SecurityMetricsController {

    @Autowired
    private SecurityMonitoringService securityMonitoringService;

    /**
     * Obtient les métriques de sécurité
     */
    @GetMapping
    public Map<String, Object> getSecurityMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("authentication_failures_total", securityMonitoringService.getAuthenticationFailures());
        metrics.put("authorization_failures_total", securityMonitoringService.getAuthorizationFailures());
        metrics.put("rate_limit_hits_total", securityMonitoringService.getRateLimitHits());
        metrics.put("suspicious_requests_total", securityMonitoringService.getSuspiciousRequests());
        metrics.put("sql_injection_attempts_total", securityMonitoringService.getSqlInjectionAttempts());
        metrics.put("xss_attempts_total", securityMonitoringService.getXssAttempts());
        
        return metrics;
    }
}

