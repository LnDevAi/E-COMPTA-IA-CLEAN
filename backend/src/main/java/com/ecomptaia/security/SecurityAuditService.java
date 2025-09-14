package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service d'audit de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Collecte et stocke les événements d'audit de sécurité
 */
@Service
public class SecurityAuditService {

    @Autowired
    private SecurityLoggingService securityLoggingService;

    private final ConcurrentLinkedQueue<SecurityAuditEntry> auditLogs = new ConcurrentLinkedQueue<>();
    private static final int MAX_AUDIT_ENTRIES = 50000;

    /**
     * Enregistre un événement d'audit de sécurité
     */
    public void auditSecurityEvent(String eventType, String userId, String clientIp, 
                                 String userAgent, String requestUri, String details) {
        SecurityAuditEntry entry = new SecurityAuditEntry(
            LocalDateTime.now(),
            eventType,
            userId,
            clientIp,
            userAgent,
            requestUri,
            details
        );
        
        auditLogs.offer(entry);
        
        // Limiter la taille du cache
        while (auditLogs.size() > MAX_AUDIT_ENTRIES) {
            auditLogs.poll();
        }
        
        // Log l'événement
        securityLoggingService.logSecurityEvent(eventType, clientIp, userAgent, requestUri, details);
    }

    /**
     * Obtient les logs d'audit de sécurité récents
     */
    public ConcurrentLinkedQueue<SecurityAuditEntry> getRecentAuditLogs() {
        return new ConcurrentLinkedQueue<>(auditLogs);
    }

    /**
     * Classe interne pour les entrées d'audit de sécurité
     */
    public static class SecurityAuditEntry {
        private final LocalDateTime timestamp;
        private final String eventType;
        private final String userId;
        private final String clientIp;
        private final String userAgent;
        private final String requestUri;
        private final String details;

        public SecurityAuditEntry(LocalDateTime timestamp, String eventType, String userId, 
                                String clientIp, String userAgent, String requestUri, String details) {
            this.timestamp = timestamp;
            this.eventType = eventType;
            this.userId = userId;
            this.clientIp = clientIp;
            this.userAgent = userAgent;
            this.requestUri = requestUri;
            this.details = details;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public String getEventType() { return eventType; }
        public String getUserId() { return userId; }
        public String getClientIp() { return clientIp; }
        public String getUserAgent() { return userAgent; }
        public String getRequestUri() { return requestUri; }
        public String getDetails() { return details; }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s - %s - %s - %s - %s",
                timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                eventType,
                userId,
                clientIp,
                userAgent,
                requestUri,
                details
            );
        }
    }
}