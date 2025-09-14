package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service de gestion des incidents de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Détecte et gère les incidents de sécurité
 */
@Service
public class SecurityIncidentService {


    @Autowired
    private SecurityLoggingService securityLoggingService;

    private final ConcurrentLinkedQueue<SecurityIncident> incidents = new ConcurrentLinkedQueue<>();
    private static final int MAX_INCIDENTS = 1000;

    /**
     * Enregistre un incident de sécurité
     */
    public void recordSecurityIncident(String incidentType, String severity, String clientIp, 
                                     String userAgent, String requestUri, String details) {
        SecurityIncident incident = new SecurityIncident(
            LocalDateTime.now(),
            incidentType,
            severity,
            clientIp,
            userAgent,
            requestUri,
            details
        );
        
        incidents.offer(incident);
        
        // Limiter la taille du cache
        while (incidents.size() > MAX_INCIDENTS) {
            incidents.poll();
        }
        
        // Log l'incident
        securityLoggingService.logSecurityEvent(incidentType, clientIp, userAgent, requestUri, details);
    }

    /**
     * Obtient les incidents de sécurité récents
     */
    public ConcurrentLinkedQueue<SecurityIncident> getRecentIncidents() {
        return new ConcurrentLinkedQueue<>(incidents);
    }

    /**
     * Classe interne pour les incidents de sécurité
     */
    public static class SecurityIncident {
        private final LocalDateTime timestamp;
        private final String incidentType;
        private final String severity;
        private final String clientIp;
        private final String userAgent;
        private final String requestUri;
        private final String details;

        public SecurityIncident(LocalDateTime timestamp, String incidentType, String severity, 
                             String clientIp, String userAgent, String requestUri, String details) {
            this.timestamp = timestamp;
            this.incidentType = incidentType;
            this.severity = severity;
            this.clientIp = clientIp;
            this.userAgent = userAgent;
            this.requestUri = requestUri;
            this.details = details;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public String getIncidentType() { return incidentType; }
        public String getSeverity() { return severity; }
        public String getClientIp() { return clientIp; }
        public String getUserAgent() { return userAgent; }
        public String getRequestUri() { return requestUri; }
        public String getDetails() { return details; }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s - %s - %s - %s - %s",
                timestamp.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                incidentType,
                severity,
                clientIp,
                userAgent,
                requestUri,
                details
            );
        }
    }
}
