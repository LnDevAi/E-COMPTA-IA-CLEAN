package com.ecomptaia.controller;

import com.ecomptaia.service.GovernmentPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/government-platforms")
@CrossOrigin(origins = "*")
public class GovernmentPlatformController {

    @Autowired
    private GovernmentPlatformService governmentPlatformService;

    // ==================== PLATEFORMES GOUVERNEMENTALES ====================

    /**
     * Récupérer les plateformes gouvernementales disponibles par pays
     */
    @GetMapping("/platforms/{countryCode}")
    public ResponseEntity<Map<String, Object>> getGovernmentPlatforms(@PathVariable String countryCode) {
        try {
            Map<String, Object> platforms = governmentPlatformService.getAvailablePlatforms(countryCode);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "countryCode", countryCode,
                "platforms", platforms,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des plateformes",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Tester la connexion à une plateforme gouvernementale
     */
    @PostMapping("/test-connection/{countryCode}")
    public ResponseEntity<Map<String, Object>> testPlatformConnection(@PathVariable String countryCode, @RequestBody Map<String, Object> request) {
        try {
            String platformType = (String) request.get("platformType");

            Map<String, Object> connectionStatus = governmentPlatformService.testConnection(
                countryCode, platformType
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "connectionStatus", connectionStatus,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors du test de connexion",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Soumettre une déclaration fiscale
     */
    @PostMapping("/submit-tax-declaration/{countryCode}")
    public ResponseEntity<Map<String, Object>> submitTaxDeclaration(@PathVariable String countryCode, @RequestBody Map<String, Object> request) {
        try {
            String declarationType = (String) request.get("declarationType");
            String period = (String) request.get("period");
            @SuppressWarnings("unchecked")
            Map<String, Object> declarationData = (Map<String, Object>) request.get("data");
            String companyId = (String) request.get("companyId");

            Map<String, Object> submissionResult = governmentPlatformService.submitTaxDeclaration(
                countryCode, declarationType, period, declarationData, Long.valueOf(companyId)
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "submissionResult", submissionResult,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la soumission",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Soumettre une déclaration sociale
     */
    @PostMapping("/submit-social-declaration/{countryCode}")
    public ResponseEntity<Map<String, Object>> submitSocialDeclaration(@PathVariable String countryCode, @RequestBody Map<String, Object> request) {
        try {
            String declarationType = (String) request.get("declarationType");
            String period = (String) request.get("period");
            @SuppressWarnings("unchecked")
            Map<String, Object> declarationData = (Map<String, Object>) request.get("data");
            String companyId = (String) request.get("companyId");

            Map<String, Object> submissionResult = governmentPlatformService.submitSocialDeclaration(
                countryCode, declarationType, period, declarationData, Long.valueOf(companyId)
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "submissionResult", submissionResult,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la soumission",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Récupérer le statut d'une déclaration
     */
    @GetMapping("/declaration-status/{submissionId}")
    public ResponseEntity<Map<String, Object>> getDeclarationStatus(@PathVariable String submissionId) {
        try {
            Map<String, Object> status = governmentPlatformService.getDeclarationStatus(submissionId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "submissionId", submissionId,
                "status", status,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la récupération du statut",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Récupérer les notifications gouvernementales
     */
    @GetMapping("/notifications/{countryCode}")
    public ResponseEntity<Map<String, Object>> getGovernmentNotifications(@PathVariable String countryCode) {
        try {
            List<Map<String, Object>> notifications = governmentPlatformService.getNotifications(countryCode);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "countryCode", countryCode,
                "notifications", notifications,
                "count", notifications.size(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des notifications",
                "message", e.getMessage()
            ));
        }
    }

    // ==================== LOGICIELS TIERS ====================

    /**
     * Tester la connexion à un logiciel tiers
     */
    @PostMapping("/test-third-party-connection")
    public ResponseEntity<Map<String, Object>> testThirdPartyConnection(@RequestBody Map<String, Object> request) {
        try {
            String softwareType = (String) request.get("softwareType");

            // Simulation de test de connexion
            Map<String, Object> connectionStatus = Map.of(
                "success", true,
                "status", "CONNECTED",
                "softwareType", softwareType,
                "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "connectionStatus", connectionStatus,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors du test de connexion",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Récupérer les statistiques d'intégration
     */
    @GetMapping("/integration-statistics")
    public ResponseEntity<Map<String, Object>> getIntegrationStatistics() {
        try {
            Map<String, Object> stats = governmentPlatformService.getIntegrationStatistics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "statistics", stats,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des statistiques",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Exécuter un test complet de toutes les plateformes
     */
    @PostMapping("/run-complete-test")
    public ResponseEntity<Map<String, Object>> runCompletePlatformTest() {
        try {
            Map<String, Object> testResults = governmentPlatformService.runCompleteTest();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "testResults", testResults,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de l'exécution du test",
                "message", e.getMessage()
            ));
        }
    }

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Test de récupération des plateformes
     */
    @PostMapping("/test/platforms")
    public ResponseEntity<Map<String, Object>> testGetPlatforms() {
        try {
            Map<String, Object> platforms = governmentPlatformService.getAvailablePlatforms("CMR");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "testType", "getAvailablePlatforms",
                "result", platforms,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors du test",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Test de connexion à une plateforme
     */
    @PostMapping("/test/connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            Map<String, Object> connectionStatus = governmentPlatformService.testConnection("CMR", "taxPlatform");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "testType", "testConnection",
                "result", connectionStatus,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors du test",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Test de soumission de déclaration fiscale
     */
    @PostMapping("/test/tax-declaration")
    public ResponseEntity<Map<String, Object>> testTaxDeclaration() {
        try {
            Map<String, Object> testData = Map.of(
                "amount", 1000000.0,
                "currency", "XAF",
                "description", "Test déclaration fiscale"
            );

            Map<String, Object> submissionResult = governmentPlatformService.submitTaxDeclaration(
                "CMR", "TVA", "2024-Q1", testData, 1L
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "testType", "submitTaxDeclaration",
                "result", submissionResult,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors du test",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Test de récupération des notifications
     */
    @PostMapping("/test/notifications")
    public ResponseEntity<Map<String, Object>> testGetNotifications() {
        try {
            List<Map<String, Object>> notifications = governmentPlatformService.getNotifications("CMR");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "testType", "getNotifications",
                "result", notifications,
                "count", notifications.size(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors du test",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Test de récupération du statut d'une déclaration
     */
    @PostMapping("/test/declaration-status")
    public ResponseEntity<Map<String, Object>> testGetDeclarationStatus() {
        try {
            String testSubmissionId = "SUB_" + System.currentTimeMillis() + "_CMR";
            Map<String, Object> status = governmentPlatformService.getDeclarationStatus(testSubmissionId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "testType", "getDeclarationStatus",
                "submissionId", testSubmissionId,
                "result", status,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors du test",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Test complet de toutes les fonctionnalités
     */
    @PostMapping("/test/complete")
    public ResponseEntity<Map<String, Object>> testCompleteFunctionality() {
        try {
            Map<String, Object> testResults = new java.util.HashMap<>();
            
            // Test 1: Récupération des plateformes
            try {
                governmentPlatformService.getAvailablePlatforms("CMR");
                testResults.put("getAvailablePlatforms", "SUCCESS");
            } catch (Exception e) {
                testResults.put("getAvailablePlatforms", "FAILED: " + e.getMessage());
            }

            // Test 2: Test de connexion
            try {
                governmentPlatformService.testConnection("CMR", "taxPlatform");
                testResults.put("testConnection", "SUCCESS");
            } catch (Exception e) {
                testResults.put("testConnection", "FAILED: " + e.getMessage());
            }

            // Test 3: Récupération des notifications
            try {
                List<Map<String, Object>> notifications = governmentPlatformService.getNotifications("CMR");
                testResults.put("getNotifications", "SUCCESS (" + notifications.size() + " notifications)");
            } catch (Exception e) {
                testResults.put("getNotifications", "FAILED: " + e.getMessage());
            }

            // Test 4: Statistiques d'intégration
            try {
                governmentPlatformService.getIntegrationStatistics();
                testResults.put("getIntegrationStatistics", "SUCCESS");
            } catch (Exception e) {
                testResults.put("getIntegrationStatistics", "FAILED: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "testType", "completeFunctionality",
                "results", testResults,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors du test complet",
                "message", e.getMessage()
            ));
        }
    }
}
