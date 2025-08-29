package com.ecomptaia.controller;

import com.ecomptaia.service.GovernmentPlatformService;
import com.ecomptaia.service.ThirdPartySoftwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/government-platform")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GovernmentPlatformController {

    @Autowired
    private GovernmentPlatformService governmentPlatformService;

    @Autowired
    private ThirdPartySoftwareService thirdPartySoftwareService;

    // ==================== APIS GOUVERNEMENTALES ====================

    /**
     * Test de base du module
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Module plateformes gouvernementales opérationnel");
        response.put("timestamp", LocalDateTime.now());
        response.put("features", List.of(
            "APIs gouvernementales (fiscales, sociales, douanes)",
            "Intégration logiciels tiers",
            "Synchronisation automatique",
            "Validation et conformité"
        ));
        return ResponseEntity.ok(response);
    }

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
            String apiKey = (String) request.get("apiKey");
            String apiSecret = (String) request.get("apiSecret");

            Map<String, Object> connectionStatus = governmentPlatformService.testConnection(
                countryCode, platformType, apiKey, apiSecret
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
     * Récupérer les logiciels tiers disponibles
     */
    @GetMapping("/third-party-software")
    public ResponseEntity<Map<String, Object>> getThirdPartySoftware() {
        try {
            List<Map<String, Object>> softwareList = thirdPartySoftwareService.getAvailableSoftware();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "softwareList", softwareList,
                "count", softwareList.size(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des logiciels",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Tester la connexion à un logiciel tiers
     */
    @PostMapping("/test-software-connection")
    public ResponseEntity<Map<String, Object>> testThirdPartyConnection(@RequestBody Map<String, Object> request) {
        try {
            String softwareName = (String) request.get("softwareName");
            String apiUrl = (String) request.get("apiUrl");
            String apiKey = (String) request.get("apiKey");
            String apiSecret = (String) request.get("apiSecret");

            Map<String, Object> connectionStatus = thirdPartySoftwareService.testConnection(
                softwareName, apiUrl, apiKey, apiSecret
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "softwareName", softwareName,
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
     * Synchroniser les données avec un logiciel tiers
     */
    @PostMapping("/sync-software-data")
    public ResponseEntity<Map<String, Object>> syncThirdPartyData(@RequestBody Map<String, Object> request) {
        try {
            String softwareName = (String) request.get("softwareName");
            String dataType = (String) request.get("dataType");
            String syncDirection = (String) request.get("syncDirection"); // INBOUND, OUTBOUND, BOTH
            Long companyId = Long.valueOf(request.get("companyId").toString());

            Map<String, Object> syncResult = thirdPartySoftwareService.syncData(
                softwareName, dataType, syncDirection, companyId
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "syncResult", syncResult,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la synchronisation",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Récupérer les logs de synchronisation
     */
    @GetMapping("/software-sync-logs/{softwareName}")
    public ResponseEntity<Map<String, Object>> getSyncLogs(@PathVariable String softwareName) {
        try {
            List<Map<String, Object>> logs = thirdPartySoftwareService.getSyncLogs(softwareName);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "softwareName", softwareName,
                "logs", logs,
                "count", logs.size(),
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des logs",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Configurer une intégration avec un logiciel tiers
     */
    @PostMapping("/configure-software-integration")
    public ResponseEntity<Map<String, Object>> configureThirdPartyIntegration(@RequestBody Map<String, Object> request) {
        try {
            String softwareName = (String) request.get("softwareName");
            String integrationType = (String) request.get("integrationType");
            Map<String, Object> configuration = (Map<String, Object>) request.get("configuration");
            Long companyId = Long.valueOf(request.get("companyId").toString());

            Map<String, Object> configResult = thirdPartySoftwareService.configureIntegration(
                softwareName, integrationType, configuration, companyId
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "configResult", configResult,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la configuration",
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
            Map<String, Object> govStats = governmentPlatformService.getIntegrationStatistics();
            Map<String, Object> thirdPartyStats = thirdPartySoftwareService.getIntegrationStatistics();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "governmentPlatforms", govStats,
                "thirdPartySoftware", thirdPartyStats,
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
     * Test complet du module
     */
    @GetMapping("/complete-test")
    public ResponseEntity<Map<String, Object>> testCompleteModule() {
        try {
            // Test des plateformes gouvernementales
            Map<String, Object> govTestResults = governmentPlatformService.runCompleteTest();
            
            // Test des logiciels tiers
            Map<String, Object> thirdPartyTestResults = thirdPartySoftwareService.runCompleteTest();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "governmentPlatforms", govTestResults,
                "thirdPartySoftware", thirdPartyTestResults,
                "overallStatus", "OPERATIONAL",
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
