package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service de détection de menaces pour E-COMPTA-IA INTERNATIONAL
 * Détecte les menaces de sécurité
 */
@Service
public class SecurityThreatDetectionService {


    @Autowired
    private SecurityLoggingService securityLoggingService;

    private final ConcurrentLinkedQueue<SecurityThreat> threats = new ConcurrentLinkedQueue<>();
    private static final int MAX_THREATS = 2000;

    /**
     * Enregistre une menace de sécurité
     */
    public void recordSecurityThreat(String threatType, String severity, String clientIp, 
                                   String userAgent, String requestUri, String details) {
        SecurityThreat threat = new SecurityThreat(
            LocalDateTime.now(),
            threatType,
            severity,
            clientIp,
            userAgent,
            requestUri,
            details
        );
        
        threats.offer(threat);
        
        // Limiter la taille du cache
        while (threats.size() > MAX_THREATS) {
            threats.poll();
        }
        
        // Log la menace
        securityLoggingService.logSecurityEvent(threatType, clientIp, userAgent, requestUri, details);
    }

    /**
     * Obtient les menaces de sécurité récentes
     */
    public ConcurrentLinkedQueue<SecurityThreat> getRecentThreats() {
        return new ConcurrentLinkedQueue<>(threats);
    }

    /**
     * Classe interne pour les menaces de sécurité
     */
    public static class SecurityThreat {
        private final LocalDateTime timestamp;
        private final String threatType;
        private final String severity;
        private final String clientIp;
        private final String userAgent;
        private final String requestUri;
        private final String details;

        public SecurityThreat(LocalDateTime timestamp, String threatType, String severity, 
                           String clientIp, String userAgent, String requestUri, String details) {
            this.timestamp = timestamp;
            this.threatType = threatType;
            this.severity = severity;
            this.clientIp = clientIp;
            this.userAgent = userAgent;
            this.requestUri = requestUri;
            this.details = details;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public String getThreatType() { return threatType; }
        public String getSeverity() { return severity; }
        public String getClientIp() { return clientIp; }
        public String getUserAgent() { return userAgent; }
        public String getRequestUri() { return requestUri; }
        public String getDetails() { return details; }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s - %s - %s - %s - %s",
                timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                threatType,
                severity,
                clientIp,
                userAgent,
                requestUri,
                details
            );
        }
    }
}
