package com.ecomptaia.security;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service de logging de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Collecte et stocke les logs de sécurité
 */
@Service
public class SecurityLoggingService {

    private final ConcurrentLinkedQueue<SecurityLogEntry> securityLogs = new ConcurrentLinkedQueue<>();
    private static final int MAX_LOG_ENTRIES = 10000;

    /**
     * Enregistre un log de sécurité
     */
    public void logSecurityEvent(String eventType, String clientIp, String userAgent, String requestUri, String details) {
        SecurityLogEntry entry = new SecurityLogEntry(
            LocalDateTime.now(),
            eventType,
            clientIp,
            userAgent,
            requestUri,
            details
        );
        
        securityLogs.offer(entry);
        
        // Limiter la taille du cache
        while (securityLogs.size() > MAX_LOG_ENTRIES) {
            securityLogs.poll();
        }
    }

    /**
     * Obtient les logs de sécurité récents
     */
    public ConcurrentLinkedQueue<SecurityLogEntry> getRecentSecurityLogs() {
        return new ConcurrentLinkedQueue<>(securityLogs);
    }

    /**
     * Classe interne pour les entrées de log de sécurité
     */
    public static class SecurityLogEntry {
        private final LocalDateTime timestamp;
        private final String eventType;
        private final String clientIp;
        private final String userAgent;
        private final String requestUri;
        private final String details;

        public SecurityLogEntry(LocalDateTime timestamp, String eventType, String clientIp, 
                              String userAgent, String requestUri, String details) {
            this.timestamp = timestamp;
            this.eventType = eventType;
            this.clientIp = clientIp;
            this.userAgent = userAgent;
            this.requestUri = requestUri;
            this.details = details;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public String getEventType() { return eventType; }
        public String getClientIp() { return clientIp; }
        public String getUserAgent() { return userAgent; }
        public String getRequestUri() { return requestUri; }
        public String getDetails() { return details; }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s - %s - %s - %s",
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                eventType,
                clientIp,
                userAgent,
                requestUri,
                details
            );
        }
    }
}

