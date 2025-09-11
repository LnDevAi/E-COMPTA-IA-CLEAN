package com.ecomptaia.controller;

import com.ecomptaia.entity.DataProtection;
import com.ecomptaia.entity.ConsentRecord;
import com.ecomptaia.entity.DataProcessingActivity;
import com.ecomptaia.service.DataProtectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur pour la gestion de la conformité RGPD/CCPA
 */
@RestController
@RequestMapping("/api/data-protection")
@CrossOrigin(origins = "*")
public class DataProtectionController {
    
    @Autowired
    private DataProtectionService dataProtectionService;
    
    /**
     * Crée une configuration de protection des données
     */
    @PostMapping("/config")
    public ResponseEntity<DataProtection> createDataProtectionConfig(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            DataProtection.RegulationType regulationType = DataProtection.RegulationType.valueOf(
                request.get("regulationType").toString());
            String dataControllerName = request.get("dataControllerName").toString();
            String dataControllerEmail = request.get("dataControllerEmail").toString();
            
            DataProtection dataProtection = dataProtectionService.createDataProtectionConfig(
                companyId, regulationType, dataControllerName, dataControllerEmail);
            
            return ResponseEntity.ok(dataProtection);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Enregistre un consentement
     */
    @PostMapping("/consent")
    public ResponseEntity<ConsentRecord> recordConsent(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String email = request.get("email").toString();
            ConsentRecord.ConsentType consentType = ConsentRecord.ConsentType.valueOf(
                request.get("consentType").toString());
            Boolean consentGiven = Boolean.valueOf(request.get("consentGiven").toString());
            ConsentRecord.ConsentMethod consentMethod = ConsentRecord.ConsentMethod.valueOf(
                request.get("consentMethod").toString());
            String ipAddress = request.get("ipAddress") != null ? request.get("ipAddress").toString() : null;
            String userAgent = request.get("userAgent") != null ? request.get("userAgent").toString() : null;
            
            ConsentRecord consentRecord = dataProtectionService.recordConsent(
                companyId, email, consentType, consentGiven, consentMethod, ipAddress, userAgent);
            
            return ResponseEntity.ok(consentRecord);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Retire un consentement
     */
    @PostMapping("/consent/{consentId}/withdraw")
    public ResponseEntity<Void> withdrawConsent(@PathVariable Long consentId, 
                                               @RequestBody Map<String, Object> request) {
        try {
            // Fonctionnalité de retrait de consentement - implémentation future
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Vérifie la conformité RGPD
     */
    @PostMapping("/compliance/gdpr")
    public ResponseEntity<Map<String, Object>> checkGDPRCompliance(@RequestBody DataProtection dataProtection) {
        Map<String, Object> compliance = dataProtectionService.checkGDPRCompliance(dataProtection);
        return ResponseEntity.ok(compliance);
    }
    
    /**
     * Vérifie la conformité CCPA
     */
    @PostMapping("/compliance/ccpa")
    public ResponseEntity<Map<String, Object>> checkCCPACompliance(@RequestBody DataProtection dataProtection) {
        Map<String, Object> compliance = dataProtectionService.checkCCPACompliance(dataProtection);
        return ResponseEntity.ok(compliance);
    }
    
    /**
     * Récupère les réglementations supportées
     */
    @GetMapping("/regulations")
    public ResponseEntity<Map<String, String>> getSupportedRegulations() {
        Map<String, String> regulations = dataProtectionService.getSupportedRegulations();
        return ResponseEntity.ok(regulations);
    }
    
    /**
     * Récupère les types de consentement supportés
     */
    @GetMapping("/consent-types")
    public ResponseEntity<Map<String, String>> getSupportedConsentTypes() {
        Map<String, String> consentTypes = dataProtectionService.getSupportedConsentTypes();
        return ResponseEntity.ok(consentTypes);
    }
    
    /**
     * Récupère les bases légales supportées
     */
    @GetMapping("/legal-bases")
    public ResponseEntity<Map<String, String>> getSupportedLegalBases() {
        Map<String, String> legalBases = dataProtectionService.getSupportedLegalBases();
        return ResponseEntity.ok(legalBases);
    }
    
    /**
     * Crée une activité de traitement des données
     */
    @PostMapping("/processing-activity")
    public ResponseEntity<DataProcessingActivity> createProcessingActivity(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String activityName = request.get("activityName").toString();
            String purpose = request.get("purpose").toString();
            DataProcessingActivity.LegalBasis legalBasis = DataProcessingActivity.LegalBasis.valueOf(
                request.get("legalBasis").toString());
            
            DataProcessingActivity activity = dataProtectionService.createProcessingActivity(
                companyId, activityName, purpose, legalBasis);
            
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
