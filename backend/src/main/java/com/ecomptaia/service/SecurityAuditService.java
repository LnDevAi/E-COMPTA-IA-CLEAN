ackage com.ecomptaia.service;

import com.ecomptaia.entity.SecurityAudit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service pour la gestion de l'audit de sécurité
 */
@Service
public class SecurityAuditService {
    
    /**
     * Enregistre un événement de sécurité
     */
    public SecurityAudit logSecurityEvent(Long companyId, Long userId, String sessionId,
                                        SecurityAudit.EventType eventType, SecurityAudit.EventCategory eventCategory,
                                        String eventDescription, SecurityAudit.RiskLevel riskLevel,
                                        Boolean isSuccessful, String ipAddress, String userAgent) {
        
        SecurityAudit audit = new SecurityAudit();
        audit.setCompanyId(companyId);
        audit.setUserId(userId);
        audit.setSessionId(sessionId);
        audit.setEventType(eventType);
        audit.setEventCategory(eventCategory);
        audit.setEventDescription(eventDescription);
        audit.setRiskLevel(riskLevel);
        audit.setIsSuccessful(isSuccessful);
        audit.setIpAddress(ipAddress);
        audit.setUserAgent(userAgent);
        audit.setCreatedAt(LocalDateTime.now());
        
        return audit;
    }
    
    /**
     * Enregistre un événement d'accès API
     */
    public SecurityAudit logApiAccess(Long companyId, Long userId, String sessionId,
                                    String endpoint, String httpMethod, Integer statusCode,
                                    Long responseTimeMs, String ipAddress, String userAgent) {
        
        SecurityAudit.EventType eventType = SecurityAudit.EventType.API_ACCESS;
        SecurityAudit.EventCategory eventCategory = SecurityAudit.EventCategory.SYSTEM;
        SecurityAudit.RiskLevel riskLevel = determineRiskLevel(statusCode, responseTimeMs);
        Boolean isSuccessful = statusCode >= 200 && statusCode < 400;
        
        String eventDescription = String.format("Accès API: %s %s - Status: %d - Temps: %dms", 
                                               httpMethod, endpoint, statusCode, responseTimeMs);
        
        SecurityAudit audit = logSecurityEvent(companyId, userId, sessionId, eventType, eventCategory,
                                             eventDescription, riskLevel, isSuccessful, ipAddress, userAgent);
        
        audit.setEndpoint(endpoint);
        audit.setHttpMethod(httpMethod);
        audit.setStatusCode(statusCode);
        audit.setResponseTimeMs(responseTimeMs);
        
        return audit;
    }
    
    /**
     * Enregistre un événement de connexion
     */
    public SecurityAudit logLoginEvent(Long companyId, Long userId, String sessionId,
                                     Boolean isSuccessful, String failureReason,
                                     String ipAddress, String userAgent) {
        
        SecurityAudit.EventType eventType = isSuccessful ? 
            SecurityAudit.EventType.LOGIN : SecurityAudit.EventType.LOGIN_FAILED;
        SecurityAudit.EventCategory eventCategory = SecurityAudit.EventCategory.AUTHENTICATION;
        SecurityAudit.RiskLevel riskLevel = isSuccessful ? 
            SecurityAudit.RiskLevel.LOW : SecurityAudit.RiskLevel.MEDIUM;
        
        String eventDescription = isSuccessful ? 
            "Connexion réussie" : "Échec de connexion: " + failureReason;
        
        SecurityAudit audit = logSecurityEvent(companyId, userId, sessionId, eventType, eventCategory,
                                             eventDescription, riskLevel, isSuccessful, ipAddress, userAgent);
        
        if (!isSuccessful) {
            audit.setFailureReason(failureReason);
        }
        
        return audit;
    }
    
    /**
     * Enregistre un événement d'accès aux données
     */
    public SecurityAudit logDataAccessEvent(Long companyId, Long userId, String sessionId,
                                          String dataType, String operation, Boolean isSuccessful,
                                          String ipAddress, String userAgent) {
        
        SecurityAudit.EventType eventType = SecurityAudit.EventType.DATA_ACCESS;
        SecurityAudit.EventCategory eventCategory = SecurityAudit.EventCategory.DATA_ACCESS;
        SecurityAudit.RiskLevel riskLevel = isSuccessful ? 
            SecurityAudit.RiskLevel.LOW : SecurityAudit.RiskLevel.MEDIUM;
        
        String eventDescription = String.format("Accès aux données: %s - Opération: %s", dataType, operation);
        
        SecurityAudit audit = logSecurityEvent(companyId, userId, sessionId, eventType, eventCategory,
                                             eventDescription, riskLevel, isSuccessful, ipAddress, userAgent);
        
        return audit;
    }
    
    /**
     * Détermine le niveau de risque basé sur le code de statut et le temps de réponse
     */
    private SecurityAudit.RiskLevel determineRiskLevel(Integer statusCode, Long responseTimeMs) {
        if (statusCode >= 500) {
            return SecurityAudit.RiskLevel.HIGH;
        } else if (statusCode >= 400) {
            return SecurityAudit.RiskLevel.MEDIUM;
        } else if (responseTimeMs != null && responseTimeMs > 5000) {
            return SecurityAudit.RiskLevel.MEDIUM;
        } else {
            return SecurityAudit.RiskLevel.LOW;
        }
    }
    
    /**
     * Analyse les événements de sécurité pour détecter des anomalies
     */
    public Map<String, Object> analyzeSecurityEvents(List<SecurityAudit> events) {
        Map<String, Object> analysis = new HashMap<>();
        
        int totalEvents = events.size();
        int failedEvents = 0;
        int criticalEvents = 0;
        int highRiskEvents = 0;
        int suspiciousActivities = 0;
        
        Map<String, Integer> eventTypeCounts = new HashMap<>();
        Map<String, Integer> ipAddressCounts = new HashMap<>();
        Map<String, Integer> userCounts = new HashMap<>();
        
        for (SecurityAudit event : events) {
            // Compter les événements par type
            eventTypeCounts.merge(event.getEventType().name(), 1, Integer::sum);
            
            // Compter les événements par IP
            if (event.getIpAddress() != null) {
                ipAddressCounts.merge(event.getIpAddress(), 1, Integer::sum);
            }
            
            // Compter les événements par utilisateur
            if (event.getUserId() != null) {
                userCounts.merge(event.getUserId().toString(), 1, Integer::sum);
            }
            
            // Compter les échecs
            if (!event.getIsSuccessful()) {
                failedEvents++;
            }
            
            // Compter les événements critiques
            if (event.isCritical()) {
                criticalEvents++;
            }
            
            // Compter les événements à haut risque
            if (event.getRiskLevel() == SecurityAudit.RiskLevel.HIGH) {
                highRiskEvents++;
            }
            
            // Détecter les activités suspectes
            if (isSuspiciousActivity(event, events)) {
                suspiciousActivities++;
            }
        }
        
        analysis.put("totalEvents", totalEvents);
        analysis.put("failedEvents", failedEvents);
        analysis.put("criticalEvents", criticalEvents);
        analysis.put("highRiskEvents", highRiskEvents);
        analysis.put("suspiciousActivities", suspiciousActivities);
        analysis.put("eventTypeCounts", eventTypeCounts);
        analysis.put("ipAddressCounts", ipAddressCounts);
        analysis.put("userCounts", userCounts);
        analysis.put("failureRate", totalEvents > 0 ? (double) failedEvents / totalEvents * 100 : 0);
        analysis.put("riskScore", calculateRiskScore(criticalEvents, highRiskEvents, failedEvents, totalEvents));
        
        return analysis;
    }
    
    /**
     * Détecte les activités suspectes
     */
    private boolean isSuspiciousActivity(SecurityAudit event, List<SecurityAudit> allEvents) {
        // Détecter les tentatives de connexion multiples échouées
        if (event.getEventType() == SecurityAudit.EventType.LOGIN_FAILED) {
            long failedLogins = allEvents.stream()
                .filter(e -> e.getEventType() == SecurityAudit.EventType.LOGIN_FAILED)
                .filter(e -> e.getIpAddress() != null && e.getIpAddress().equals(event.getIpAddress()))
                .filter(e -> e.getCreatedAt().isAfter(event.getCreatedAt().minusHours(1)))
                .count();
            
            return failedLogins >= 5; // 5 tentatives échouées en 1 heure
        }
        
        // Détecter les accès à des heures inhabituelles
        if (event.getEventType() == SecurityAudit.EventType.DATA_ACCESS) {
            int hour = event.getCreatedAt().getHour();
            return hour < 6 || hour > 22; // Accès entre 22h et 6h
        }
        
        return false;
    }
    
    /**
     * Calcule un score de risque global
     */
    private int calculateRiskScore(int criticalEvents, int highRiskEvents, int failedEvents, int totalEvents) {
        if (totalEvents == 0) return 0;
        
        int score = 0;
        score += criticalEvents * 10; // Événements critiques = 10 points
        score += highRiskEvents * 5;  // Événements à haut risque = 5 points
        score += failedEvents * 2;    // Événements échoués = 2 points
        
        // Normaliser sur 100
        return Math.min(100, score);
    }
    
    /**
     * Génère un rapport de sécurité
     */
    public Map<String, Object> generateSecurityReport(List<SecurityAudit> events, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> report = new HashMap<>();
        
        report.put("reportPeriod", Map.of(
            "startDate", startDate,
            "endDate", endDate
        ));
        
        report.put("summary", analyzeSecurityEvents(events));
        
        // Top 10 des événements les plus fréquents
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) report.get("summary");
        @SuppressWarnings("unchecked")
        Map<String, Integer> eventTypeCounts = (Map<String, Integer>) summary.get("eventTypeCounts");
        List<Map.Entry<String, Integer>> topEvents = new ArrayList<>(eventTypeCounts.entrySet());
        topEvents.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        report.put("topEvents", topEvents.stream().limit(10).toList());
        
        // Recommandations de sécurité
        List<String> recommendations = generateSecurityRecommendations(events);
        report.put("recommendations", recommendations);
        
        return report;
    }
    
    /**
     * Génère des recommandations de sécurité
     */
    private List<String> generateSecurityRecommendations(List<SecurityAudit> events) {
        List<String> recommendations = new ArrayList<>();
        
        long failedLogins = events.stream()
            .filter(e -> e.getEventType() == SecurityAudit.EventType.LOGIN_FAILED)
            .count();
        
        if (failedLogins > 10) {
            recommendations.add("Considérer l'implémentation d'un système de verrouillage de compte après plusieurs tentatives échouées");
        }
        
        long criticalEvents = events.stream()
            .filter(SecurityAudit::isCritical)
            .count();
        
        if (criticalEvents > 0) {
            recommendations.add("Investiguer immédiatement les événements critiques détectés");
        }
        
        long suspiciousActivities = events.stream()
            .filter(e -> e.getEventType() == SecurityAudit.EventType.SUSPICIOUS_ACTIVITY)
            .count();
        
        if (suspiciousActivities > 0) {
            recommendations.add("Renforcer la surveillance des activités suspectes détectées");
        }
        
        return recommendations;
    }
}
