package com.ecomptaia.controller;

import com.ecomptaia.service.MobileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile")
@CrossOrigin(origins = "*")
public class MobileController {

    @Autowired
    private MobileService mobileService;

    // ==================== AUTHENTIFICATION MOBILE ====================

    /**
     * Connexion mobile optimisée
     */
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> mobileLogin(
            @RequestBody Map<String, Object> credentials) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = mobileService.mobileLogin(credentials);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Vérification du token mobile
     */
    @PostMapping("/auth/verify")
    public ResponseEntity<Map<String, Object>> verifyMobileToken(
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = mobileService.verifyMobileToken(token);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Rafraîchissement du token
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<Map<String, Object>> refreshMobileToken(
            @RequestHeader("Authorization") String refreshToken) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = mobileService.refreshMobileToken(refreshToken);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== DASHBOARD MOBILE ====================

    /**
     * Dashboard mobile optimisé
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getMobileDashboard(
            @RequestParam Long companyId,
            @RequestParam(required = false) String deviceType) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> dashboard = mobileService.getMobileDashboard(companyId, deviceType);
            response.put("success", true);
            response.put("data", dashboard);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * KPIs mobiles
     */
    @GetMapping("/dashboard/kpis")
    public ResponseEntity<Map<String, Object>> getMobileKPIs(
            @RequestParam Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> kpis = mobileService.getMobileKPIs(companyId);
            response.put("success", true);
            response.put("data", kpis);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ÉCRITURES MOBILES ====================

    /**
     * Liste des écritures pour mobile
     */
    @GetMapping("/ecritures")
    public ResponseEntity<Map<String, Object>> getMobileEcritures(
            @RequestParam Long companyId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> ecritures = mobileService.getMobileEcritures(companyId, page, size, status);
            response.put("success", true);
            response.put("data", ecritures);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Détail d'une écriture pour mobile
     */
    @GetMapping("/ecritures/{id}")
    public ResponseEntity<Map<String, Object>> getMobileEcritureDetail(
            @PathVariable Long id,
            @RequestParam Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> ecriture = mobileService.getMobileEcritureDetail(id, companyId);
            response.put("success", true);
            response.put("data", ecriture);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Création d'écriture mobile
     */
    @PostMapping("/ecritures/create")
    public ResponseEntity<Map<String, Object>> createMobileEcriture(
            @RequestParam Long companyId,
            @RequestBody Map<String, Object> ecritureData) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = mobileService.createMobileEcriture(companyId, ecritureData);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== COMPTES MOBILES ====================

    /**
     * Liste des comptes pour mobile
     */
    @GetMapping("/comptes")
    public ResponseEntity<Map<String, Object>> getMobileComptes(
            @RequestParam Long companyId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "50") int size) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> comptes = mobileService.getMobileComptes(companyId, page, size);
            response.put("success", true);
            response.put("data", comptes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Recherche de comptes mobile
     */
    @GetMapping("/comptes/search")
    public ResponseEntity<Map<String, Object>> searchMobileComptes(
            @RequestParam Long companyId,
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = mobileService.searchMobileComptes(companyId, query, limit);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== TIERS MOBILES ====================

    /**
     * Liste des tiers pour mobile
     */
    @GetMapping("/tiers")
    public ResponseEntity<Map<String, Object>> getMobileTiers(
            @RequestParam Long companyId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "30") int size,
            @RequestParam(required = false) String type) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> tiers = mobileService.getMobileTiers(companyId, page, size, type);
            response.put("success", true);
            response.put("data", tiers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Détail d'un tiers pour mobile
     */
    @GetMapping("/tiers/{id}")
    public ResponseEntity<Map<String, Object>> getMobileTierDetail(
            @PathVariable Long id,
            @RequestParam Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> tier = mobileService.getMobileTierDetail(id, companyId);
            response.put("success", true);
            response.put("data", tier);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== DOCUMENTS MOBILES ====================

    /**
     * Liste des documents pour mobile
     */
    @GetMapping("/documents")
    public ResponseEntity<Map<String, Object>> getMobileDocuments(
            @RequestParam Long companyId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> documents = mobileService.getMobileDocuments(companyId, page, size);
            response.put("success", true);
            response.put("data", documents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Upload de document mobile
     */
    @PostMapping("/documents/upload")
    public ResponseEntity<Map<String, Object>> uploadMobileDocument(
            @RequestParam Long companyId,
            @RequestParam Long userId,
            @RequestBody Map<String, Object> documentData) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = mobileService.uploadMobileDocument(companyId, userId, documentData);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== NOTIFICATIONS MOBILES ====================

    /**
     * Notifications mobiles
     */
    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getMobileNotifications(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> notifications = mobileService.getMobileNotifications(userId, page, size);
            response.put("success", true);
            response.put("data", notifications);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Marquer notification comme lue
     */
    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<Map<String, Object>> markNotificationAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = mobileService.markNotificationAsRead(id, userId);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== SYNCHRONISATION MOBILE ====================

    /**
     * Synchronisation des données
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncMobileData(
            @RequestParam Long companyId,
            @RequestParam Long userId,
            @RequestBody Map<String, Object> syncData) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = mobileService.syncMobileData(companyId, userId, syncData);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Statut de synchronisation
     */
    @GetMapping("/sync/status")
    public ResponseEntity<Map<String, Object>> getSyncStatus(
            @RequestParam Long companyId,
            @RequestParam Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> status = mobileService.getSyncStatus(companyId, userId);
            response.put("success", true);
            response.put("data", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== CONFIGURATION MOBILE ====================

    /**
     * Configuration mobile
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getMobileConfig(
            @RequestParam Long companyId,
            @RequestParam(required = false) String deviceType) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> config = mobileService.getMobileConfig(companyId, deviceType);
            response.put("success", true);
            response.put("data", config);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Mise à jour de la configuration
     */
    @PostMapping("/config/update")
    public ResponseEntity<Map<String, Object>> updateMobileConfig(
            @RequestParam Long userId,
            @RequestBody Map<String, Object> configData) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = mobileService.updateMobileConfig(userId, configData);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Test de l'API mobile
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testMobileAPI() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> testData = mobileService.getTestMobileData();
            response.put("success", true);
            response.put("message", "API mobile opérationnelle");
            response.put("data", testData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors du test : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}







