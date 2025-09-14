package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service de génération de rapports de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Génère des rapports de sécurité pour la surveillance
 */
@Service
public class SecurityReportService {

    @Autowired
    private SecurityMonitoringService securityMonitoringService;

    @Autowired
    private SecurityLoggingService securityLoggingService;

    @Autowired
    private SecurityAuditService securityAuditService;

    @Autowired
    private SecurityIncidentService securityIncidentService;

    /**
     * Génère un rapport de sécurité complet
     */
    public Map<String, Object> generateSecurityReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Métriques de sécurité
        Map<String, Object> securityMetrics = new HashMap<>();
        securityMetrics.put("authentication_failures", securityMonitoringService.getAuthenticationFailures());
        securityMetrics.put("authorization_failures", securityMonitoringService.getAuthorizationFailures());
        securityMetrics.put("rate_limit_hits", securityMonitoringService.getRateLimitHits());
        securityMetrics.put("suspicious_requests", securityMonitoringService.getSuspiciousRequests());
        securityMetrics.put("sql_injection_attempts", securityMonitoringService.getSqlInjectionAttempts());
        securityMetrics.put("xss_attempts", securityMonitoringService.getXssAttempts());
        
        report.put("security_metrics", securityMetrics);
        
        // Statistiques des logs
        Map<String, Object> logStats = new HashMap<>();
        logStats.put("total_security_logs", securityLoggingService.getRecentSecurityLogs().size());
        logStats.put("total_audit_logs", securityAuditService.getRecentAuditLogs().size());
        logStats.put("total_incidents", securityIncidentService.getRecentIncidents().size());
        
        report.put("log_statistics", logStats);
        
        // Timestamp du rapport
        report.put("report_timestamp", LocalDateTime.now());
        
        return report;
    }
}

