package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service d'alerte de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Gère les alertes de sécurité
 */
@Service
public class SecurityAlertService {


    @Autowired
    private SecurityLoggingService securityLoggingService;

    private final ConcurrentLinkedQueue<SecurityAlert> alerts = new ConcurrentLinkedQueue<>();
    private static final int MAX_ALERTS = 5000;

    /**
     * Enregistre une alerte de sécurité
     */
    public void recordSecurityAlert(String alertType, String severity, String clientIp, 
                                  String userAgent, String requestUri, String details) {
        SecurityAlert alert = new SecurityAlert(
            LocalDateTime.now(),
            alertType,
            severity,
            clientIp,
            userAgent,
            requestUri,
            details
        );
        
        alerts.offer(alert);
        
        // Limiter la taille du cache
        while (alerts.size() > MAX_ALERTS) {
            alerts.poll();
        }
        
        // Log l'alerte
        securityLoggingService.logSecurityEvent(alertType, clientIp, userAgent, requestUri, details);
    }

    /**
     * Obtient les alertes de sécurité récentes
     */
    public ConcurrentLinkedQueue<SecurityAlert> getRecentAlerts() {
        return new ConcurrentLinkedQueue<>(alerts);
    }

    /**
     * Classe interne pour les alertes de sécurité
     */
    public static class SecurityAlert {
        private final LocalDateTime timestamp;
        private final String alertType;
        private final String severity;
        private final String clientIp;
        private final String userAgent;
        private final String requestUri;
        private final String details;

        public SecurityAlert(LocalDateTime timestamp, String alertType, String severity, 
                           String clientIp, String userAgent, String requestUri, String details) {
            this.timestamp = timestamp;
            this.alertType = alertType;
            this.severity = severity;
            this.clientIp = clientIp;
            this.userAgent = userAgent;
            this.requestUri = requestUri;
            this.details = details;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public String getAlertType() { return alertType; }
        public String getSeverity() { return severity; }
        public String getClientIp() { return clientIp; }
        public String getUserAgent() { return userAgent; }
        public String getRequestUri() { return requestUri; }
        public String getDetails() { return details; }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s - %s - %s - %s - %s",
                timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                alertType,
                severity,
                clientIp,
                userAgent,
                requestUri,
                details
            );
        }
    }
}
