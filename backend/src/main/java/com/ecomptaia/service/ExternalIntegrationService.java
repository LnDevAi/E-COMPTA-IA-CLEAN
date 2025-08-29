package com.ecomptaia.service;

import com.ecomptaia.entity.ExternalIntegration;
import com.ecomptaia.entity.WebhookEvent;
import com.ecomptaia.entity.SocialDeclaration;
import com.ecomptaia.repository.ExternalIntegrationRepository;
import com.ecomptaia.repository.WebhookEventRepository;
import com.ecomptaia.repository.SocialDeclarationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class ExternalIntegrationService {

    @Autowired
    private ExternalIntegrationRepository externalIntegrationRepository;

    @Autowired
    private WebhookEventRepository webhookEventRepository;

    @Autowired
    private SocialDeclarationRepository socialDeclarationRepository;

    @Autowired
    private RestTemplate restTemplate;

    // ==================== GESTION DES INTÉGRATIONS EXTERNES ====================

    /**
     * Créer une nouvelle intégration externe
     */
    public ExternalIntegration createIntegration(String integrationName, 
                                               ExternalIntegration.IntegrationType integrationType,
                                               String providerName, Long entrepriseId) {
        ExternalIntegration integration = new ExternalIntegration(integrationName, integrationType, providerName, entrepriseId);
        return externalIntegrationRepository.save(integration);
    }

    /**
     * Activer une intégration
     */
    public ExternalIntegration activateIntegration(Long integrationId) {
        ExternalIntegration integration = externalIntegrationRepository.findById(integrationId)
                .orElseThrow(() -> new RuntimeException("Intégration non trouvée"));
        integration.setIsActive(true);
        integration.setSyncStatus(ExternalIntegration.SyncStatus.PENDING);
        return externalIntegrationRepository.save(integration);
    }

    /**
     * Désactiver une intégration
     */
    public ExternalIntegration deactivateIntegration(Long integrationId) {
        ExternalIntegration integration = externalIntegrationRepository.findById(integrationId)
                .orElseThrow(() -> new RuntimeException("Intégration non trouvée"));
        integration.setIsActive(false);
        integration.setSyncStatus(ExternalIntegration.SyncStatus.DISABLED);
        return externalIntegrationRepository.save(integration);
    }

    /**
     * Synchroniser une intégration
     */
    @Async
    public CompletableFuture<Boolean> syncIntegration(Long integrationId) {
        try {
            ExternalIntegration integration = externalIntegrationRepository.findById(integrationId)
                    .orElseThrow(() -> new RuntimeException("Intégration non trouvée"));

            integration.setSyncStatus(ExternalIntegration.SyncStatus.IN_PROGRESS);
            externalIntegrationRepository.save(integration);

            // Simulation de synchronisation
            Thread.sleep(2000);

            integration.setSyncStatus(ExternalIntegration.SyncStatus.COMPLETED);
            integration.setLastSync(LocalDateTime.now());
            integration.setNextSync(LocalDateTime.now().plusMinutes(integration.getSyncFrequency()));
            externalIntegrationRepository.save(integration);

            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            ExternalIntegration integration = externalIntegrationRepository.findById(integrationId).orElse(null);
            if (integration != null) {
                integration.setSyncStatus(ExternalIntegration.SyncStatus.FAILED);
                integration.setErrorCount(integration.getErrorCount() + 1);
                integration.setLastError(e.getMessage());
                externalIntegrationRepository.save(integration);
            }
            return CompletableFuture.completedFuture(false);
        }
    }

    // ==================== GESTION DES WEBHOOKS ====================

    /**
     * Créer un événement webhook
     */
    public WebhookEvent createWebhookEvent(String eventType, String eventSource, String payload, 
                                         String webhookUrl, Long entrepriseId) {
        WebhookEvent event = new WebhookEvent(eventType, eventSource, payload, webhookUrl, entrepriseId);
        return webhookEventRepository.save(event);
    }

    /**
     * Traiter les événements webhook en attente
     */
    @Scheduled(fixedRate = 30000) // Toutes les 30 secondes
    public void processWebhookEvents() {
        List<WebhookEvent> pendingEvents = webhookEventRepository.findByStatusAndRetryCountLessThan(
                WebhookEvent.EventStatus.PENDING, 3);

        for (WebhookEvent event : pendingEvents) {
            processWebhookEvent(event);
        }
    }

    /**
     * Traiter un événement webhook
     */
    private void processWebhookEvent(WebhookEvent event) {
        try {
            event.setStatus(WebhookEvent.EventStatus.PROCESSING);
            webhookEventRepository.save(event);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(event.getPayload(), headers);
            long startTime = System.currentTimeMillis();

            ResponseEntity<String> response = restTemplate.exchange(
                    event.getWebhookUrl(), 
                    HttpMethod.POST, 
                    entity, 
                    String.class
            );

            long processingTime = System.currentTimeMillis() - startTime;

            event.setStatus(WebhookEvent.EventStatus.COMPLETED);
            event.setResponseCode(response.getStatusCode().value());
            event.setResponseBody(response.getBody());
            event.setProcessingTime(processingTime);
            event.setProcessedAt(LocalDateTime.now());

        } catch (Exception e) {
            event.setStatus(WebhookEvent.EventStatus.FAILED);
            event.setErrorMessage(e.getMessage());
            event.setRetryCount(event.getRetryCount() + 1);
            
            if (event.getRetryCount() < event.getMaxRetries()) {
                event.setStatus(WebhookEvent.EventStatus.RETRY);
                event.setNextRetry(LocalDateTime.now().plusMinutes(5));
            }
        }

        webhookEventRepository.save(event);
    }

    // ==================== GESTION DES DÉCLARATIONS SOCIALES ====================

    /**
     * Créer une déclaration sociale
     */
    public SocialDeclaration createSocialDeclaration(SocialDeclaration.DeclarationType declarationType,
                                                   Integer periodMonth, Integer periodYear, Long entrepriseId) {
        SocialDeclaration declaration = new SocialDeclaration(declarationType, periodMonth, periodYear, entrepriseId);
        return socialDeclarationRepository.save(declaration);
    }

    /**
     * Soumettre une déclaration sociale
     */
    public SocialDeclaration submitSocialDeclaration(Long declarationId) {
        SocialDeclaration declaration = socialDeclarationRepository.findById(declarationId)
                .orElseThrow(() -> new RuntimeException("Déclaration non trouvée"));

        try {
            // Simulation de soumission à la plateforme officielle
            declaration.setSubmissionStatus(SocialDeclaration.SubmissionStatus.SUBMITTED);
            declaration.setSubmissionDate(LocalDateTime.now());
            declaration.setReferenceNumber("REF-" + System.currentTimeMillis());

            // Simulation de validation
            Thread.sleep(1000);
            declaration.setSubmissionStatus(SocialDeclaration.SubmissionStatus.VALIDATED);
            declaration.setValidationDate(LocalDateTime.now());
            declaration.setReceiptNumber("REC-" + System.currentTimeMillis());

        } catch (Exception e) {
            declaration.setSubmissionStatus(SocialDeclaration.SubmissionStatus.REJECTED);
            declaration.setRejectionReason("Erreur lors de la soumission: " + e.getMessage());
        }

        return socialDeclarationRepository.save(declaration);
    }

    /**
     * Générer automatiquement les déclarations sociales mensuelles
     */
    @Scheduled(cron = "0 0 1 * * ?") // Le 1er de chaque mois à minuit
    public void generateMonthlySocialDeclarations() {
        LocalDateTime now = LocalDateTime.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // Générer les déclarations pour toutes les entreprises
        List<Long> entrepriseIds = Arrays.asList(1L, 2L, 3L); // À adapter selon vos données

        for (Long entrepriseId : entrepriseIds) {
            // CNSS
            createSocialDeclaration(SocialDeclaration.DeclarationType.CNSS, currentMonth, currentYear, entrepriseId);
            
            // IPRES
            createSocialDeclaration(SocialDeclaration.DeclarationType.IPRES, currentMonth, currentYear, entrepriseId);
            
            // FDFP
            createSocialDeclaration(SocialDeclaration.DeclarationType.FDFP, currentMonth, currentYear, entrepriseId);
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Obtenir les statistiques des intégrations
     */
    public Map<String, Object> getIntegrationStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalIntegrations", externalIntegrationRepository.countByEntrepriseId(entrepriseId));
        stats.put("activeIntegrations", externalIntegrationRepository.countByEntrepriseIdAndIsActiveTrue(entrepriseId));
        stats.put("failedIntegrations", externalIntegrationRepository.findIntegrationsWithErrors().size());
        
        return stats;
    }

    /**
     * Obtenir les statistiques des webhooks
     */
    public Map<String, Object> getWebhookStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalEvents", webhookEventRepository.countByEntrepriseId(entrepriseId));
        stats.put("pendingEvents", webhookEventRepository.countByEntrepriseIdAndStatus(entrepriseId, WebhookEvent.EventStatus.PENDING));
        stats.put("failedEvents", webhookEventRepository.countByEntrepriseIdAndStatus(entrepriseId, WebhookEvent.EventStatus.FAILED));
        
        return stats;
    }

    /**
     * Obtenir les statistiques des déclarations sociales
     */
    public Map<String, Object> getSocialDeclarationStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalDeclarations", socialDeclarationRepository.countByEntrepriseId(entrepriseId));
        stats.put("draftDeclarations", socialDeclarationRepository.countByEntrepriseIdAndSubmissionStatus(entrepriseId, SocialDeclaration.SubmissionStatus.DRAFT));
        stats.put("validatedDeclarations", socialDeclarationRepository.countByEntrepriseIdAndSubmissionStatus(entrepriseId, SocialDeclaration.SubmissionStatus.VALIDATED));
        stats.put("rejectedDeclarations", socialDeclarationRepository.countByEntrepriseIdAndSubmissionStatus(entrepriseId, SocialDeclaration.SubmissionStatus.REJECTED));
        
        return stats;
    }
}
