package com.ecomptaia.service;

import com.ecomptaia.entity.SecurityAudit;
import com.ecomptaia.entity.SecurityPolicy;
import com.ecomptaia.repository.SecurityAuditRepository;
import com.ecomptaia.repository.SecurityPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class AdvancedSecurityService {

    @Autowired
    private SecurityAuditRepository securityAuditRepository;

    @Autowired
    private SecurityPolicyRepository securityPolicyRepository;

    // Cache pour les sessions actives
    private final Map<String, SessionInfo> activeSessions = new ConcurrentHashMap<>();
    
    // Cache pour les tentatives de connexion échouées
    private final Map<String, List<LocalDateTime>> failedLoginAttempts = new ConcurrentHashMap<>();
    
    // Cache pour les IPs suspectes
    private final Set<String> suspiciousIPs = ConcurrentHashMap.newKeySet();

    // ==================== AUDIT TRAIL ====================

    /**
     * Créer un enregistrement d'audit
     */
    public SecurityAudit createAuditLog(Long userId, String username, String action, String resource, 
                                      String ipAddress, String userAgent, String sessionId, 
                                      SecurityAudit.AuditStatus status, SecurityAudit.RiskLevel riskLevel, 
                                      String details, Long entrepriseId) {
        
        SecurityAudit audit = new SecurityAudit(userId, username, action, resource);
        audit.setIpAddress(ipAddress);
        audit.setUserAgent(userAgent);
        audit.setSessionId(sessionId);
        audit.setStatus(status);
        audit.setRiskLevel(riskLevel);
        audit.setDetails(details);
        audit.setEntrepriseId(entrepriseId);
        
        return securityAuditRepository.save(audit);
    }

    /**
     * Créer un audit de connexion
     */
    public SecurityAudit logLoginAttempt(Long userId, String username, String ipAddress, 
                                       String userAgent, String sessionId, boolean success, 
                                       String details, Long entrepriseId) {
        
        String action = success ? "LOGIN_SUCCESS" : "LOGIN_FAILURE";
        SecurityAudit.AuditStatus status = success ? SecurityAudit.AuditStatus.SUCCESS : SecurityAudit.AuditStatus.FAILURE;
        SecurityAudit.RiskLevel riskLevel = success ? SecurityAudit.RiskLevel.LOW : SecurityAudit.RiskLevel.MEDIUM;
        
        return createAuditLog(userId, username, action, "/api/auth/login", ipAddress, userAgent, 
                            sessionId, status, riskLevel, details, entrepriseId);
    }

    // ==================== GESTION DES SESSIONS ====================

    /**
     * Créer une nouvelle session
     */
    public String createSession(Long userId, String username, String ipAddress, String userAgent) {
        String sessionId = generateSessionId();
        SessionInfo sessionInfo = new SessionInfo(userId, username, ipAddress, userAgent);
        activeSessions.put(sessionId, sessionInfo);
        
        return sessionId;
    }

    /**
     * Valider une session
     */
    public boolean validateSession(String sessionId) {
        SessionInfo sessionInfo = activeSessions.get(sessionId);
        if (sessionInfo == null) {
            return false;
        }
        
        // Vérifier si la session n'a pas expiré (30 minutes)
        if (sessionInfo.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now())) {
            activeSessions.remove(sessionId);
            return false;
        }
        
        // Mettre à jour le dernier accès
        sessionInfo.setLastAccess(LocalDateTime.now());
        return true;
    }

    /**
     * Invalider une session
     */
    public void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }

    // ==================== DÉTECTION D'INTRUSION ====================

    /**
     * Vérifier les tentatives de connexion échouées
     */
    public boolean checkFailedLoginAttempts(String username, String ipAddress) {
        String key = username + ":" + ipAddress;
        List<LocalDateTime> attempts = failedLoginAttempts.get(key);
        
        if (attempts == null) {
            return false;
        }
        
        // Nettoyer les tentatives anciennes (plus de 15 minutes)
        attempts.removeIf(attempt -> attempt.plusMinutes(15).isBefore(LocalDateTime.now()));
        
        // Vérifier si trop de tentatives (5 tentatives en 15 minutes)
        return attempts.size() >= 5;
    }

    /**
     * Enregistrer une tentative de connexion échouée
     */
    public void recordFailedLoginAttempt(String username, String ipAddress) {
        String key = username + ":" + ipAddress;
        failedLoginAttempts.computeIfAbsent(key, k -> new ArrayList<>()).add(LocalDateTime.now());
        
        // Marquer l'IP comme suspecte si trop d'échecs
        if (checkFailedLoginAttempts(username, ipAddress)) {
            suspiciousIPs.add(ipAddress);
        }
    }

    /**
     * Vérifier si une IP est suspecte
     */
    public boolean isSuspiciousIP(String ipAddress) {
        return suspiciousIPs.contains(ipAddress);
    }

    // ==================== POLITIQUES DE SÉCURITÉ ====================

    /**
     * Créer une politique de sécurité
     */
    public SecurityPolicy createSecurityPolicy(String policyName, SecurityPolicy.PolicyType policyType, 
                                             String description, String conditions, String actions, 
                                             Integer priority, Long createdBy, Long entrepriseId) {
        
        SecurityPolicy policy = new SecurityPolicy(policyName, policyType, description);
        policy.setConditions(conditions);
        policy.setActions(actions);
        policy.setPriority(priority != null ? priority : 1);
        policy.setCreatedBy(createdBy);
        policy.setEntrepriseId(entrepriseId);
        
        return securityPolicyRepository.save(policy);
    }

    // ==================== RECHERCHE ET RAPPORTS ====================

    /**
     * Obtenir les statistiques de sécurité
     */
    public Map<String, Object> getSecurityStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques d'audit
        stats.put("totalAudits", securityAuditRepository.countByEntrepriseId(entrepriseId));
        stats.put("successAudits", securityAuditRepository.countByEntrepriseIdAndStatus(entrepriseId, SecurityAudit.AuditStatus.SUCCESS));
        stats.put("failureAudits", securityAuditRepository.countByEntrepriseIdAndStatus(entrepriseId, SecurityAudit.AuditStatus.FAILURE));
        stats.put("blockedAudits", securityAuditRepository.countByEntrepriseIdAndStatus(entrepriseId, SecurityAudit.AuditStatus.BLOCKED));
        
        // Statistiques des politiques
        stats.put("totalPolicies", securityPolicyRepository.countByEntrepriseId(entrepriseId));
        stats.put("activePolicies", securityPolicyRepository.countActiveByEntrepriseId(entrepriseId));
        
        // Sessions actives
        stats.put("activeSessions", activeSessions.size());
        stats.put("suspiciousIPs", suspiciousIPs.size());
        
        return stats;
    }

    // ==================== UTILITAIRES ====================

    /**
     * Générer un ID de session unique
     */
    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Nettoyer les sessions expirées
     */
    @Scheduled(fixedRate = 600000) // Toutes les 10 minutes
    public void cleanupExpiredSessions() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);
            activeSessions.entrySet().removeIf(entry -> 
                entry.getValue().getCreatedAt().isBefore(cutoffTime));
            
            System.out.println("🧹 Nettoyage des sessions expirées terminé");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du nettoyage des sessions: " + e.getMessage());
        }
    }

    // ==================== CLASSE INTERNE ====================

    public static class SessionInfo {
        private Long userId;
        private String username;
        private String ipAddress;
        private String userAgent;
        private LocalDateTime createdAt;
        private LocalDateTime lastAccess;

        public SessionInfo(Long userId, String username, String ipAddress, String userAgent) {
            this.userId = userId;
            this.username = username;
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
            this.createdAt = LocalDateTime.now();
            this.lastAccess = LocalDateTime.now();
        }

        // Getters et setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getLastAccess() { return lastAccess; }
        public void setLastAccess(LocalDateTime lastAccess) { this.lastAccess = lastAccess; }
    }
}
