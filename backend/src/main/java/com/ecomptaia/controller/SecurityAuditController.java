package com.ecomptaia.controller;

import com.ecomptaia.entity.SecurityAudit;
import com.ecomptaia.service.SecurityAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion de l'audit de sécurité
 */
@RestController
@RequestMapping("/api/security-audit")
@CrossOrigin(origins = "*")
public class SecurityAuditController {
    
    @Autowired
    private SecurityAuditService securityAuditService;
    
    /**
     * Enregistre un événement de sécurité
     */
    @PostMapping("/log-event")
    public ResponseEntity<SecurityAudit> logSecurityEvent(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            Long userId = request.get("userId") != null ? Long.valueOf(request.get("userId").toString()) : null;
            String sessionId = request.get("sessionId") != null ? request.get("sessionId").toString() : null;
            SecurityAudit.EventType eventType = SecurityAudit.EventType.valueOf(
                request.get("eventType").toString());
            SecurityAudit.EventCategory eventCategory = SecurityAudit.EventCategory.valueOf(
                request.get("eventCategory").toString());
            String eventDescription = request.get("eventDescription").toString();
            SecurityAudit.RiskLevel riskLevel = SecurityAudit.RiskLevel.valueOf(
                request.get("riskLevel").toString());
            Boolean isSuccessful = Boolean.valueOf(request.get("isSuccessful").toString());
            String ipAddress = request.get("ipAddress") != null ? request.get("ipAddress").toString() : null;
            String userAgent = request.get("userAgent") != null ? request.get("userAgent").toString() : null;
            
            SecurityAudit audit = securityAuditService.logSecurityEvent(
                companyId, userId, sessionId, eventType, eventCategory, eventDescription, 
                riskLevel, isSuccessful, ipAddress, userAgent);
            
            return ResponseEntity.ok(audit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Enregistre un événement d'accès API
     */
    @PostMapping("/log-api-access")
    public ResponseEntity<SecurityAudit> logApiAccess(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            Long userId = request.get("userId") != null ? Long.valueOf(request.get("userId").toString()) : null;
            String sessionId = request.get("sessionId") != null ? request.get("sessionId").toString() : null;
            String endpoint = request.get("endpoint").toString();
            String httpMethod = request.get("httpMethod").toString();
            Integer statusCode = Integer.valueOf(request.get("statusCode").toString());
            Long responseTimeMs = request.get("responseTimeMs") != null ? 
                Long.valueOf(request.get("responseTimeMs").toString()) : null;
            String ipAddress = request.get("ipAddress") != null ? request.get("ipAddress").toString() : null;
            String userAgent = request.get("userAgent") != null ? request.get("userAgent").toString() : null;
            
            SecurityAudit audit = securityAuditService.logApiAccess(
                companyId, userId, sessionId, endpoint, httpMethod, statusCode, 
                responseTimeMs, ipAddress, userAgent);
            
            return ResponseEntity.ok(audit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Enregistre un événement de connexion
     */
    @PostMapping("/log-login")
    public ResponseEntity<SecurityAudit> logLoginEvent(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            Long userId = request.get("userId") != null ? Long.valueOf(request.get("userId").toString()) : null;
            String sessionId = request.get("sessionId") != null ? request.get("sessionId").toString() : null;
            Boolean isSuccessful = Boolean.valueOf(request.get("isSuccessful").toString());
            String failureReason = request.get("failureReason") != null ? 
                request.get("failureReason").toString() : null;
            String ipAddress = request.get("ipAddress") != null ? request.get("ipAddress").toString() : null;
            String userAgent = request.get("userAgent") != null ? request.get("userAgent").toString() : null;
            
            SecurityAudit audit = securityAuditService.logLoginEvent(
                companyId, userId, sessionId, isSuccessful, failureReason, ipAddress, userAgent);
            
            return ResponseEntity.ok(audit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Enregistre un événement d'accès aux données
     */
    @PostMapping("/log-data-access")
    public ResponseEntity<SecurityAudit> logDataAccessEvent(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            Long userId = request.get("userId") != null ? Long.valueOf(request.get("userId").toString()) : null;
            String sessionId = request.get("sessionId") != null ? request.get("sessionId").toString() : null;
            String dataType = request.get("dataType").toString();
            String operation = request.get("operation").toString();
            Boolean isSuccessful = Boolean.valueOf(request.get("isSuccessful").toString());
            String ipAddress = request.get("ipAddress") != null ? request.get("ipAddress").toString() : null;
            String userAgent = request.get("userAgent") != null ? request.get("userAgent").toString() : null;
            
            SecurityAudit audit = securityAuditService.logDataAccessEvent(
                companyId, userId, sessionId, dataType, operation, isSuccessful, ipAddress, userAgent);
            
            return ResponseEntity.ok(audit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Analyse les événements de sécurité
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeSecurityEvents(@RequestBody List<SecurityAudit> events) {
        Map<String, Object> analysis = securityAuditService.analyzeSecurityEvents(events);
        return ResponseEntity.ok(analysis);
    }
    
    /**
     * Génère un rapport de sécurité
     */
    @PostMapping("/report")
    public ResponseEntity<Map<String, Object>> generateSecurityReport(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<SecurityAudit> events = (List<SecurityAudit>) request.get("events");
            LocalDateTime startDate = LocalDateTime.parse(request.get("startDate").toString());
            LocalDateTime endDate = LocalDateTime.parse(request.get("endDate").toString());
            
            Map<String, Object> report = securityAuditService.generateSecurityReport(events, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Récupère les types d'événements supportés
     */
    @GetMapping("/event-types")
    public ResponseEntity<List<Map<String, String>>> getEventTypes() {
        List<Map<String, String>> types = Arrays.stream(SecurityAudit.EventType.values())
                .map(t -> Map.of("code", t.name(), "label", t.name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }
    
    /**
     * Récupère les catégories d'événements supportées
     */
    @GetMapping("/event-categories")
    public ResponseEntity<List<Map<String, String>>> getEventCategories() {
        List<Map<String, String>> categories = Arrays.stream(SecurityAudit.EventCategory.values())
                .map(c -> Map.of("code", c.name(), "label", c.name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Récupère les niveaux de risque supportés
     */
    @GetMapping("/risk-levels")
    public ResponseEntity<Map<String, String>> getSupportedRiskLevels() {
        Map<String, String> riskLevels = new java.util.HashMap<>();
        for (SecurityAudit.RiskLevel level : SecurityAudit.RiskLevel.values()) {
            riskLevels.put(level.name(), level.getDescription());
        }
        return ResponseEntity.ok(riskLevels);
    }
}




