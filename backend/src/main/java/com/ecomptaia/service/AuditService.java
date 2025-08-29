package com.ecomptaia.service;

import com.ecomptaia.entity.AuditLog;
import com.ecomptaia.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service pour l'audit trail - Traçabilité de toutes les actions importantes
 */
@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Enregistrer une action d'audit
     */
    public AuditLog logAction(AuditLog.ActionType actionType, String entityType, String description) {
        return logAction(actionType, entityType, null, description, null, null);
    }

    /**
     * Enregistrer une action d'audit avec entité
     */
    public AuditLog logAction(AuditLog.ActionType actionType, String entityType, Long entityId, String description) {
        return logAction(actionType, entityType, entityId, description, null, null);
    }

    /**
     * Enregistrer une action d'audit avec utilisateur
     */
    public AuditLog logAction(AuditLog.ActionType actionType, String entityType, String description, Long userId, String username) {
        return logAction(actionType, entityType, null, description, userId, username);
    }

    /**
     * Enregistrer une action d'audit complète
     */
    public AuditLog logAction(AuditLog.ActionType actionType, String entityType, Long entityId, 
                             String description, Long userId, String username) {
        return logAction(actionType, entityType, entityId, description, userId, username, null, null);
    }

    /**
     * Enregistrer une action d'audit avec anciennes et nouvelles valeurs
     */
    public AuditLog logAction(AuditLog.ActionType actionType, String entityType, Long entityId, 
                             String description, Long userId, String username, 
                             Object oldValues, Object newValues) {
        
        AuditLog auditLog = new AuditLog(actionType, entityType, description);
        auditLog.setEntityId(entityId);
        auditLog.setUserId(userId);
        auditLog.setUsername(username);
        
        // Récupérer les informations de la requête HTTP
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                auditLog.setIpAddress(getClientIpAddress(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
                auditLog.setSessionId(request.getSession().getId());
            }
        } catch (Exception e) {
            // Ignorer les erreurs de récupération des informations de requête
        }
        
        // Sérialiser les anciennes et nouvelles valeurs
        if (oldValues != null) {
            try {
                auditLog.setOldValues(objectMapper.writeValueAsString(oldValues));
            } catch (JsonProcessingException e) {
                auditLog.setOldValues("Erreur de sérialisation: " + e.getMessage());
            }
        }
        
        if (newValues != null) {
            try {
                auditLog.setNewValues(objectMapper.writeValueAsString(newValues));
            } catch (JsonProcessingException e) {
                auditLog.setNewValues("Erreur de sérialisation: " + e.getMessage());
            }
        }
        
        return auditLogRepository.save(auditLog);
    }

    /**
     * Enregistrer une action d'audit avec entreprise
     */
    public AuditLog logAction(AuditLog.ActionType actionType, String entityType, Long entityId, 
                             String description, Long userId, String username, Long companyId) {
        
        AuditLog auditLog = logAction(actionType, entityType, entityId, description, userId, username);
        auditLog.setCompanyId(companyId);
        return auditLogRepository.save(auditLog);
    }

    /**
     * Enregistrer une action d'audit avec statut d'échec
     */
    public AuditLog logFailedAction(AuditLog.ActionType actionType, String entityType, String description, 
                                   String errorMessage, Long userId, String username) {
        
        AuditLog auditLog = logAction(actionType, entityType, description, userId, username);
        auditLog.setStatus(AuditLog.AuditStatus.FAILED);
        auditLog.setErrorMessage(errorMessage);
        return auditLogRepository.save(auditLog);
    }

    /**
     * Obtenir l'adresse IP du client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    // === MÉTHODES DE RECHERCHE ===

    /**
     * Obtenir les logs d'audit par utilisateur
     */
    public List<AuditLog> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * Obtenir les logs d'audit par entreprise
     */
    public List<AuditLog> getAuditLogsByCompany(Long companyId) {
        return auditLogRepository.findByCompanyIdOrderByTimestampDesc(companyId);
    }

    /**
     * Obtenir les logs d'audit par type d'action
     */
    public List<AuditLog> getAuditLogsByActionType(AuditLog.ActionType actionType) {
        return auditLogRepository.findByActionTypeOrderByTimestampDesc(actionType);
    }

    /**
     * Obtenir les logs d'audit par période
     */
    public List<AuditLog> getAuditLogsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(startDate, endDate);
    }

    /**
     * Obtenir les logs d'audit par entité
     */
    public List<AuditLog> getAuditLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    /**
     * Obtenir les événements de sécurité
     */
    public List<AuditLog> getSecurityEvents(LocalDateTime since) {
        return auditLogRepository.findSecurityEventsSince(since);
    }

    /**
     * Obtenir les modifications d'entités
     */
    public List<AuditLog> getEntityModifications(String entityType, LocalDateTime since) {
        return auditLogRepository.findEntityModificationsSince(entityType, since);
    }

    /**
     * Obtenir les actions d'abonnement
     */
    public List<AuditLog> getSubscriptionActions(LocalDateTime since) {
        return auditLogRepository.findSubscriptionActionsSince(since);
    }

    /**
     * Obtenir les actions de paiement
     */
    public List<AuditLog> getPaymentActions(LocalDateTime since) {
        return auditLogRepository.findPaymentActionsSince(since);
    }

    /**
     * Obtenir les actions de document
     */
    public List<AuditLog> getDocumentActions(LocalDateTime since) {
        return auditLogRepository.findDocumentActionsSince(since);
    }

    // === MÉTHODES DE RAPPORT ===

    /**
     * Générer un rapport d'activité utilisateur
     */
    public Map<String, Object> generateUserActivityReport(Long userId, LocalDateTime since) {
        Map<String, Object> report = new HashMap<>();
        
        List<AuditLog> userLogs = auditLogRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(
            userId, since, LocalDateTime.now());
        
        report.put("userId", userId);
        report.put("period", since.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " - " + 
                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        report.put("totalActions", userLogs.size());
        report.put("actions", userLogs);
        
        // Statistiques par type d'action
        Map<String, Long> actionStats = new HashMap<>();
        for (AuditLog log : userLogs) {
            String actionType = log.getActionType().toString();
            actionStats.put(actionType, actionStats.getOrDefault(actionType, 0L) + 1);
        }
        report.put("actionStats", actionStats);
        
        return report;
    }

    /**
     * Générer un rapport d'activité entreprise
     */
    public Map<String, Object> generateCompanyActivityReport(Long companyId, LocalDateTime since) {
        Map<String, Object> report = new HashMap<>();
        
        List<AuditLog> companyLogs = auditLogRepository.findByCompanyIdAndTimestampBetweenOrderByTimestampDesc(
            companyId, since, LocalDateTime.now());
        
        report.put("companyId", companyId);
        report.put("period", since.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " - " + 
                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        report.put("totalActions", companyLogs.size());
        report.put("actions", companyLogs);
        
        // Statistiques par utilisateur
        Map<String, Long> userStats = new HashMap<>();
        for (AuditLog log : companyLogs) {
            String username = log.getUsername() != null ? log.getUsername() : "Unknown";
            userStats.put(username, userStats.getOrDefault(username, 0L) + 1);
        }
        report.put("userStats", userStats);
        
        return report;
    }

    /**
     * Générer un rapport de sécurité
     */
    public Map<String, Object> generateSecurityReport(LocalDateTime since) {
        Map<String, Object> report = new HashMap<>();
        
        List<AuditLog> securityEvents = auditLogRepository.findSecurityEventsSince(since);
        List<Object[]> suspiciousLogins = auditLogRepository.findSuspiciousLoginAttempts(since, 5L);
        
        report.put("period", since.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " - " + 
                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        report.put("totalSecurityEvents", securityEvents.size());
        report.put("securityEvents", securityEvents);
        report.put("suspiciousLogins", suspiciousLogins);
        
        // Statistiques par type d'événement de sécurité
        Map<String, Long> eventStats = new HashMap<>();
        for (AuditLog log : securityEvents) {
            String actionType = log.getActionType().toString();
            eventStats.put(actionType, eventStats.getOrDefault(actionType, 0L) + 1);
        }
        report.put("eventStats", eventStats);
        
        return report;
    }

    /**
     * Générer un rapport d'audit complet
     */
    public Map<String, Object> generateCompleteAuditReport(LocalDateTime since) {
        Map<String, Object> report = new HashMap<>();
        
        report.put("period", since.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " - " + 
                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        try {
            // Statistiques générales
            List<Object[]> actionTypeStats = auditLogRepository.getActionTypeStatsSince(since);
            List<Object[]> entityTypeStats = auditLogRepository.getEntityTypeStatsSince(since);
            List<Object[]> userActivityStats = auditLogRepository.getUserActivityStatsSince(since);
            List<Object[]> ipActivityStats = auditLogRepository.getIpActivityStatsSince(since);
            
            report.put("actionTypeStats", actionTypeStats);
            report.put("entityTypeStats", entityTypeStats);
            report.put("userActivityStats", userActivityStats);
            report.put("ipActivityStats", ipActivityStats);
            
            // Statistiques temporelles (peuvent échouer avec H2)
            try {
                List<Object[]> dailyStats = auditLogRepository.getDailyStatsSince(since);
                report.put("dailyStats", dailyStats);
            } catch (Exception e) {
                report.put("dailyStats", new ArrayList<>());
                report.put("dailyStatsError", "Non supporté par H2");
            }
            
            try {
                List<Object[]> hourlyStats = auditLogRepository.getHourlyStatsSince(since);
                report.put("hourlyStats", hourlyStats);
            } catch (Exception e) {
                report.put("hourlyStats", new ArrayList<>());
                report.put("hourlyStatsError", "Non supporté par H2");
            }
            
        } catch (Exception e) {
            report.put("error", "Erreur lors de la génération des statistiques: " + e.getMessage());
            report.put("actionTypeStats", new ArrayList<>());
            report.put("entityTypeStats", new ArrayList<>());
            report.put("userActivityStats", new ArrayList<>());
            report.put("ipActivityStats", new ArrayList<>());
            report.put("dailyStats", new ArrayList<>());
            report.put("hourlyStats", new ArrayList<>());
        }
        
        // Actions récentes
        try {
            List<AuditLog> recentActions = auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(
                since, LocalDateTime.now());
            if (!recentActions.isEmpty()) {
                report.put("recentActions", recentActions.subList(0, Math.min(100, recentActions.size())));
            } else {
                report.put("recentActions", new ArrayList<>());
            }
        } catch (Exception e) {
            report.put("recentActions", new ArrayList<>());
            report.put("recentActionsError", "Erreur: " + e.getMessage());
        }
        
        return report;
    }

    /**
     * Obtenir les statistiques d'audit
     */
    public Map<String, Object> getAuditStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        
        stats.put("totalLogs", auditLogRepository.count());
        stats.put("logsLast24Hours", auditLogRepository.countUserActionsSince(1L, last24Hours)); // Utilise un userId fictif
        stats.put("logsLast7Days", auditLogRepository.countUserActionsSince(1L, last7Days));
        stats.put("logsLast30Days", auditLogRepository.countUserActionsSince(1L, last30Days));
        
        // Actions par type
        List<Object[]> actionStats = auditLogRepository.getActionTypeStatsSince(last7Days);
        stats.put("actionStatsLast7Days", actionStats);
        
        // Utilisateurs les plus actifs
        List<Object[]> userStats = auditLogRepository.getUserActivityStatsSince(last7Days);
        stats.put("userStatsLast7Days", userStats);
        
        return stats;
    }
}
