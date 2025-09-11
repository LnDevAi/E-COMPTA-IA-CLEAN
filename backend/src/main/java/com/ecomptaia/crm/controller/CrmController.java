ackage com.ecomptaia.crm.controller;

import com.ecomptaia.security.entity.User;

import com.ecomptaia.crm.entity.CrmCustomer;
import com.ecomptaia.crm.service.CrmCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour le module CRM
 * Gestion des profils clients, analytics et intelligence artificielle
 */
@RestController
@RequestMapping("/api/crm")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('USER')")
@Slf4j
public class CrmController {
    
    private final CrmCustomerService crmCustomerService;
    
    public CrmController(CrmCustomerService crmCustomerService) {
        this.crmCustomerService = crmCustomerService;
    }
    
    // === GESTION DES PROFILS CLIENTS ===
    
    /**
     * Récupération paginée des clients CRM
     */
    @GetMapping("/customers")
    public ResponseEntity<Page<CrmCustomer>> getCustomers(
            @RequestParam Long companyId,
            Pageable pageable) {
        // log.info("Récupération clients CRM pour companyId: {}", companyId);
        Page<CrmCustomer> customers = crmCustomerService.getCustomers(companyId, pageable);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Récupération d'un client par ID
     */
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CrmCustomer> getCustomer(@PathVariable Long customerId) {
        // log.info("Récupération client CRM: {}", customerId);
        CrmCustomer customer = crmCustomerService.getCustomer(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        return ResponseEntity.ok(customer);
    }
    
    /**
     * Création d'un profil CRM à partir d'un ThirdParty existant
     */
    @PostMapping("/customers/create-from-third-party")
    public ResponseEntity<CrmCustomer> createCustomerFromThirdParty(
            @RequestParam Long companyId,
            @RequestParam Long thirdPartyId) {
        // log.info("Création profil CRM depuis ThirdParty: {} pour companyId: {}", thirdPartyId, companyId);
        CrmCustomer customer = crmCustomerService.createCustomerProfile(companyId, thirdPartyId);
        return ResponseEntity.ok(customer);
    }
    
    /**
     * Création ou mise à jour d'un profil client
     */
    @PostMapping("/customers")
    public ResponseEntity<CrmCustomer> createOrUpdateCustomerProfile(
            @RequestParam Long companyId,
            @RequestBody CrmCustomer customerData) {
        // log.info("Création/mise à jour profil client pour companyId: {}", companyId);
        CrmCustomer customer = crmCustomerService.createOrUpdateCustomerProfile(companyId, customerData);
        return ResponseEntity.ok(customer);
    }
    
    /**
     * Mise à jour d'un client existant
     */
    @PutMapping("/customers/{customerId}")
    public ResponseEntity<CrmCustomer> updateCustomer(
            @PathVariable Long customerId,
            @RequestBody CrmCustomer customerData) {
        // log.info("Mise à jour client CRM: {}", customerId);
        CrmCustomer customer = crmCustomerService.updateCustomer(customerId, customerData);
        return ResponseEntity.ok(customer);
    }
    
    /**
     * Suppression d'un client
     */
    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
        // log.info("Suppression client CRM: {}", customerId);
        crmCustomerService.deleteCustomer(customerId);
        return ResponseEntity.ok("Client supprimé avec succès");
    }
    
    // === GESTION DES IDENTIFIANTS EXTERNES ===
    
    /**
     * Ajouter un ID externe à un client
     */
    @PostMapping("/customers/{customerId}/external-ids")
    public ResponseEntity<Map<String, Object>> addExternalId(
            @PathVariable Long customerId,
            @RequestParam String externalId,
            @RequestParam String externalSystem) {
        // log.info("Ajout ID externe pour client: {}", customerId);
        Map<String, Object> result = crmCustomerService.addExternalId(customerId, externalId, externalSystem);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Mise à jour des handles des réseaux sociaux
     */
    @PutMapping("/customers/{customerId}/social-media")
    public ResponseEntity<Map<String, Object>> updateSocialMediaHandles(
            @PathVariable Long customerId,
            @RequestBody Map<String, String> socialHandles) {
        // log.info("Mise à jour handles sociaux pour client: {}", customerId);
        Map<String, Object> result = crmCustomerService.updateSocialMediaHandles(customerId, socialHandles);
        return ResponseEntity.ok(result);
    }
    
    // === INTELLIGENCE ARTIFICIELLE ET ANALYTICS ===
    
    /**
     * Mise à jour de l'intelligence client pour tous les clients
     */
    @PostMapping("/customers/update-intelligence")
    public ResponseEntity<String> updateAllCustomerIntelligence(@RequestParam Long companyId) {
        // log.info("Mise à jour intelligence pour tous les clients de companyId: {}", companyId);
        crmCustomerService.updateAllCustomerIntelligence(companyId);
        return ResponseEntity.ok("Intelligence mise à jour avec succès");
    }
    
    /**
     * Obtenir les clients à haut risque de churn
     */
    @GetMapping("/customers/high-churn-risk")
    public ResponseEntity<List<CrmCustomer>> getHighChurnRiskCustomers(@RequestParam Long companyId) {
        // log.info("Récupération clients à haut risque de churn pour companyId: {}", companyId);
        List<CrmCustomer> customers = crmCustomerService.getHighChurnRiskCustomers(companyId);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Obtenir les clients à haute valeur
     */
    @GetMapping("/customers/high-value")
    public ResponseEntity<List<CrmCustomer>> getHighValueCustomers(@RequestParam Long companyId) {
        // log.info("Récupération clients à haute valeur pour companyId: {}", companyId);
        List<CrmCustomer> customers = crmCustomerService.getHighValueCustomers(companyId);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Obtenir les clients inactifs
     */
    @GetMapping("/customers/inactive")
    public ResponseEntity<List<CrmCustomer>> getInactiveCustomers(@RequestParam Long companyId) {
        // log.info("Récupération clients inactifs pour companyId: {}", companyId);
        List<CrmCustomer> customers = crmCustomerService.getInactiveCustomers(companyId);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Recherche de clients
     */
    @GetMapping("/customers/search")
    public ResponseEntity<List<CrmCustomer>> searchCustomers(
            @RequestParam Long companyId,
            @RequestParam String searchTerm) {
        // log.info("Recherche clients pour companyId: {} avec terme: {}", companyId, searchTerm);
        List<CrmCustomer> customers = crmCustomerService.searchCustomers(companyId, searchTerm);
        return ResponseEntity.ok(customers);
    }
    
    /**
     * Obtenir les clients éligibles pour une campagne
     */
    @GetMapping("/customers/eligible-for-campaign")
    public ResponseEntity<List<CrmCustomer>> getEligibleCustomersForCampaign(
            @RequestParam Long companyId,
            @RequestParam String campaignType) {
        // log.info("Récupération clients éligibles pour campagne: {} pour companyId: {}", campaignType, companyId);
        List<CrmCustomer> customers = crmCustomerService.getEligibleCustomersForCampaign(companyId, campaignType);
        return ResponseEntity.ok(customers);
    }
    
    // === ANALYTICS ET RAPPORTS ===
    
    /**
     * Obtenir les analytics de segmentation
     */
    @GetMapping("/analytics/segmentation")
    public ResponseEntity<Map<String, Object>> getSegmentAnalytics(@RequestParam Long companyId) {
        // log.info("Récupération analytics de segmentation pour companyId: {}", companyId);
        Map<String, Object> analytics = crmCustomerService.getSegmentAnalytics(companyId);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Obtenir les analytics de comportement de paiement
     */
    @GetMapping("/analytics/payment-behavior")
    public ResponseEntity<Map<String, Object>> getPaymentBehaviorAnalytics(@RequestParam Long companyId) {
        // log.info("Récupération analytics de comportement de paiement pour companyId: {}", companyId);
        Map<String, Object> analytics = crmCustomerService.getPaymentBehaviorAnalytics(companyId);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Obtenir la tendance d'activité d'achat
     */
    @GetMapping("/analytics/purchase-activity-trend")
    public ResponseEntity<Map<String, Object>> getPurchaseActivityTrend(@RequestParam Long companyId) {
        // log.info("Récupération tendance d'activité d'achat pour companyId: {}", companyId);
        Map<String, Object> trend = crmCustomerService.getPurchaseActivityTrend(companyId);
        return ResponseEntity.ok(trend);
    }
}

