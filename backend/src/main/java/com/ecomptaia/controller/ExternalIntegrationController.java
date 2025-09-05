package com.ecomptaia.controller;

import com.ecomptaia.entity.ExternalIntegration;
import com.ecomptaia.entity.WebhookEvent;
import com.ecomptaia.entity.SocialDeclaration;
import com.ecomptaia.service.ExternalIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external-integration")
@CrossOrigin(origins = "*")
public class ExternalIntegrationController {

    @Autowired
    private ExternalIntegrationService externalIntegrationService;

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Test de base du module
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Module d'intégration externe opérationnel");
        response.put("timestamp", LocalDateTime.now());
        response.put("features", List.of(
            "Intégrations externes (bancaires, fiscales, sociales)",
            "Webhooks et notifications",
            "Déclarations sociales automatiques",
            "Synchronisation multi-plateformes"
        ));
        return ResponseEntity.ok(response);
    }

    /**
     * Test de création d'intégration bancaire
     */
    @PostMapping("/test-create-banking-integration")
    public ResponseEntity<Map<String, Object>> testCreateBankingIntegration() {
        Map<String, Object> response = new HashMap<>();
        try {
            ExternalIntegration integration = externalIntegrationService.createIntegration(
                "Intégration Bancaire Test", 
                ExternalIntegration.IntegrationType.BANKING,
                "Banque Test", 
                1L
            );

            response.put("success", true);
            response.put("message", "Intégration bancaire créée avec succès");
            response.put("integration", integration);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test de création d'intégration sociale
     */
    @PostMapping("/test-create-social-integration")
    public ResponseEntity<Map<String, Object>> testCreateSocialIntegration() {
        Map<String, Object> response = new HashMap<>();
        try {
            ExternalIntegration integration = externalIntegrationService.createIntegration(
                "Intégration CNSS Test", 
                ExternalIntegration.IntegrationType.SOCIAL,
                "CNSS", 
                1L
            );

            response.put("success", true);
            response.put("message", "Intégration sociale CNSS créée avec succès");
            response.put("integration", integration);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test de création d'événement webhook
     */
    @PostMapping("/test-create-webhook")
    public ResponseEntity<Map<String, Object>> testCreateWebhook() {
        Map<String, Object> response = new HashMap<>();
        try {
            String payload = "{\"event\":\"test\",\"data\":\"test data\",\"timestamp\":\"" + LocalDateTime.now() + "\"}";
            
            WebhookEvent event = externalIntegrationService.createWebhookEvent(
                "test.event", 
                "test.source", 
                payload, 
                "https://webhook.site/test", 
                1L
            );

            response.put("success", true);
            response.put("message", "Événement webhook créé avec succès");
            response.put("event", event);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test de création de déclaration sociale CNSS
     */
    @PostMapping("/test-create-cnss-declaration")
    public ResponseEntity<Map<String, Object>> testCreateCNSSDeclaration() {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            SocialDeclaration declaration = externalIntegrationService.createSocialDeclaration(
                SocialDeclaration.DeclarationType.CNSS,
                now.getMonthValue(),
                now.getYear(),
                1L
            );

            response.put("success", true);
            response.put("message", "Déclaration CNSS créée avec succès");
            response.put("declaration", declaration);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test de soumission de déclaration sociale
     */
    @PostMapping("/test-submit-declaration/{declarationId}")
    public ResponseEntity<Map<String, Object>> testSubmitDeclaration(@PathVariable Long declarationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            SocialDeclaration declaration = externalIntegrationService.submitSocialDeclaration(declarationId);

            response.put("success", true);
            response.put("message", "Déclaration soumise avec succès");
            response.put("declaration", declaration);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la soumission: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test des statistiques complètes
     */
    @GetMapping("/test-statistics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testStatistics(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> integrationStats = externalIntegrationService.getIntegrationStatistics(entrepriseId);
            Map<String, Object> webhookStats = externalIntegrationService.getWebhookStatistics(entrepriseId);
            Map<String, Object> socialStats = externalIntegrationService.getSocialDeclarationStatistics(entrepriseId);

            response.put("success", true);
            response.put("message", "Statistiques récupérées avec succès");
            response.put("integrationStats", integrationStats);
            response.put("webhookStats", webhookStats);
            response.put("socialStats", socialStats);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    // ==================== ENDPOINTS DE GESTION ====================

    /**
     * Créer une intégration externe
     */
    @PostMapping("/integrations")
    public ResponseEntity<Map<String, Object>> createIntegration(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String integrationName = (String) request.get("integrationName");
            String integrationTypeStr = (String) request.get("integrationType");
            String providerName = (String) request.get("providerName");
            Long entrepriseId = Long.valueOf(request.get("entrepriseId").toString());

            ExternalIntegration.IntegrationType integrationType = ExternalIntegration.IntegrationType.valueOf(integrationTypeStr);

            ExternalIntegration integration = externalIntegrationService.createIntegration(
                integrationName, integrationType, providerName, entrepriseId
            );

            response.put("success", true);
            response.put("message", "Intégration créée avec succès");
            response.put("integration", integration);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Activer une intégration
     */
    @PutMapping("/integrations/{integrationId}/activate")
    public ResponseEntity<Map<String, Object>> activateIntegration(@PathVariable Long integrationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            ExternalIntegration integration = externalIntegrationService.activateIntegration(integrationId);
            response.put("success", true);
            response.put("message", "Intégration activée avec succès");
            response.put("integration", integration);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'activation: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Créer une déclaration sociale
     */
    @PostMapping("/social-declarations")
    public ResponseEntity<Map<String, Object>> createSocialDeclaration(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String declarationTypeStr = (String) request.get("declarationType");
            Integer periodMonth = (Integer) request.get("periodMonth");
            Integer periodYear = (Integer) request.get("periodYear");
            Long entrepriseId = Long.valueOf(request.get("entrepriseId").toString());

            SocialDeclaration.DeclarationType declarationType = SocialDeclaration.DeclarationType.valueOf(declarationTypeStr);

            SocialDeclaration declaration = externalIntegrationService.createSocialDeclaration(
                declarationType, periodMonth, periodYear, entrepriseId
            );

            response.put("success", true);
            response.put("message", "Déclaration sociale créée avec succès");
            response.put("declaration", declaration);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Soumettre une déclaration sociale
     */
    @PostMapping("/social-declarations/{declarationId}/submit")
    public ResponseEntity<Map<String, Object>> submitSocialDeclaration(@PathVariable Long declarationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            SocialDeclaration declaration = externalIntegrationService.submitSocialDeclaration(declarationId);
            response.put("success", true);
            response.put("message", "Déclaration soumise avec succès");
            response.put("declaration", declaration);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la soumission: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}







