package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service du tableau de bord de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Fournit les données pour le tableau de bord de sécurité
 */
@Service
public class SecurityDashboardService {

    @Autowired
    private SecurityMonitoringService securityMonitoringService;

    @Autowired
    private SecurityLoggingService securityLoggingService;

    @Autowired
    private SecurityAuditService securityAuditService;

    @Autowired
    private SecurityIncidentService securityIncidentService;

    /**
     * Obtient les données du tableau de bord de sécurité
     */
    public Map<String, Object> getSecurityDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Métriques de sécurité en temps réel
        Map<String, Object> realTimeMetrics = new HashMap<>();
        realTimeMetrics.put("authentication_failures", securityMonitoringService.getAuthenticationFailures());
        realTimeMetrics.put("authorization_failures", securityMonitoringService.getAuthorizationFailures());
        realTimeMetrics.put("rate_limit_hits", securityMonitoringService.getRateLimitHits());
        realTimeMetrics.put("suspicious_requests", securityMonitoringService.getSuspiciousRequests());
        realTimeMetrics.put("sql_injection_attempts", securityMonitoringService.getSqlInjectionAttempts());
        realTimeMetrics.put("xss_attempts", securityMonitoringService.getXssAttempts());
        
        dashboard.put("real_time_metrics", realTimeMetrics);
        
        // Statistiques des logs
        Map<String, Object> logStatistics = new HashMap<>();
        logStatistics.put("total_security_logs", securityLoggingService.getRecentSecurityLogs().size());
        logStatistics.put("total_audit_logs", securityAuditService.getRecentAuditLogs().size());
        logStatistics.put("total_incidents", securityIncidentService.getRecentIncidents().size());
        
        dashboard.put("log_statistics", logStatistics);
        
        // Timestamp du tableau de bord
        dashboard.put("dashboard_timestamp", LocalDateTime.now());
        
        return dashboard;
    }
}

