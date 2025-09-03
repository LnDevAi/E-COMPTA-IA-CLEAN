package com.ecomptaia.controller;

import com.ecomptaia.entity.SecurityAudit;
import com.ecomptaia.service.AdvancedSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/advanced-security")
@CrossOrigin(origins = "*")
public class AdvancedSecurityController {

    @Autowired
    private AdvancedSecurityService advancedSecurityService;

    // ==================== TEST ====================

    /**
     * Endpoint de test simple
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Module de sécurité avancée opérationnel");
        response.put("timestamp", LocalDateTime.now());
        response.put("features", new String[]{
            "Audit trail complet",
            "Gestion des sessions",
            "Détection d'intrusion",
            "Politiques de sécurité",
            "Statistiques de sécurité",
            "Notifications de sécurité"
        });
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test pour créer un audit
     */
    @PostMapping("/test-create-audit")
    public ResponseEntity<Map<String, Object>> testCreateAudit() {
        Map<String, Object> response = new HashMap<>();
        try {
            SecurityAudit audit = advancedSecurityService.createAuditLog(1L, "test.user", "TEST_ACTION", 
                                                                       "/api/test", "127.0.0.1", "TestAgent", 
                                                                       "test-session", SecurityAudit.AuditStatus.SUCCESS, 
                                                                       SecurityAudit.RiskLevel.LOW, 
                                                                       "Test d'audit", 1L);
            
            response.put("success", true);
            response.put("message", "Audit de test créé avec succès");
            response.put("audit", audit);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création de l'audit: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test pour créer une session
     */
    @PostMapping("/test-create-session")
    public ResponseEntity<Map<String, Object>> testCreateSession() {
        Map<String, Object> response = new HashMap<>();
        try {
            String sessionId = advancedSecurityService.createSession(1L, "test.user", "127.0.0.1", "TestAgent");
            
            response.put("success", true);
            response.put("message", "Session de test créée avec succès");
            response.put("sessionId", sessionId);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création de la session: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test pour les statistiques
     */
    @GetMapping("/test-stats/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testStats(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = advancedSecurityService.getSecurityStatistics(entrepriseId);
            
            response.put("success", true);
            response.put("message", "Statistiques de sécurité récupérées avec succès");
            response.put("stats", stats);
            response.put("entrepriseId", entrepriseId);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des stats: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }
}
