package com.ecomptaia.security;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SecurityAuditService {

    private final Map<String, SecurityEvent> securityEvents = new ConcurrentHashMap<>();
    private final AtomicLong eventCounter = new AtomicLong(0);

    public void logSecurityEvent(String eventType, String details, String userId, String ipAddress) {
        SecurityEvent event = new SecurityEvent(
            eventCounter.incrementAndGet(),
            eventType,
            details,
            userId,
            ipAddress,
            LocalDateTime.now()
        );
        securityEvents.put(event.getId().toString(), event);
        System.out.println("SECURITY_AUDIT: " + event.toString());
    }

    public void logAuthenticationSuccess(String userId, String ipAddress) {
        logSecurityEvent("AUTH_SUCCESS", "Authentification réussie", userId, ipAddress);
    }

    public void logAuthenticationFailure(String userId, String ipAddress, String reason) {
        logSecurityEvent("AUTH_FAILURE", "Échec authentification: " + reason, userId, ipAddress);
    }

    public void logAuthorizationFailure(String userId, String ipAddress, String resource, String action) {
        logSecurityEvent("AUTHZ_FAILURE", 
            "Accès refusé à " + resource + " pour l'action " + action, userId, ipAddress);
    }

    public void logSecurityViolation(String userId, String ipAddress, String violation, String details) {
        logSecurityEvent("SECURITY_VIOLATION", 
            "Violation de sécurité: " + violation + " - " + details, userId, ipAddress);
    }

    public void logRateLimitExceeded(String userId, String ipAddress) {
        logSecurityEvent("RATE_LIMIT_EXCEEDED", "Limite de débit dépassée", userId, ipAddress);
    }

    public Map<String, Object> getSecurityStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        long totalEvents = securityEvents.size();
        long authSuccess = securityEvents.values().stream()
            .filter(e -> "AUTH_SUCCESS".equals(e.getEventType())).count();
        long authFailure = securityEvents.values().stream()
            .filter(e -> "AUTH_FAILURE".equals(e.getEventType())).count();
        long violations = securityEvents.values().stream()
            .filter(e -> "SECURITY_VIOLATION".equals(e.getEventType())).count();
        
        stats.put("totalEvents", totalEvents);
        stats.put("authSuccess", authSuccess);
        stats.put("authFailure", authFailure);
        stats.put("violations", violations);
        stats.put("lastEventTime", securityEvents.values().stream()
            .mapToLong(e -> e.getTimestamp().toEpochSecond(java.time.ZoneOffset.UTC))
            .max().orElse(0));
        
        return stats;
    }

    public String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }

    public String getCurrentIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // Ignore les erreurs
        }
        return "unknown";
    }

    private static class SecurityEvent {
        private final Long id;
        private final String eventType;
        private final String details;
        private final String userId;
        private final String ipAddress;
        private final LocalDateTime timestamp;

        public SecurityEvent(Long id, String eventType, String details, String userId, String ipAddress, LocalDateTime timestamp) {
            this.id = id;
            this.eventType = eventType;
            this.details = details;
            this.userId = userId;
            this.ipAddress = ipAddress;
            this.timestamp = timestamp;
        }

        public Long getId() { return id; }
        public String getEventType() { return eventType; }
        @SuppressWarnings("unused")
        public String getDetails() { return details; }
        @SuppressWarnings("unused")
        public String getUserId() { return userId; }
        @SuppressWarnings("unused")
        public String getIpAddress() { return ipAddress; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("[%s] %s - User: %s, IP: %s, Details: %s", 
                timestamp, eventType, userId, ipAddress, details);
        }
    }
}
