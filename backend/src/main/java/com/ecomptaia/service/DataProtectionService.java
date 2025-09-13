package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service pour la gestion de la conformité RGPD/CCPA
 */
@Service
public class DataProtectionService {
    
    /**
     * Crée une configuration de protection des données pour une entreprise
     */
    public DataProtection createDataProtectionConfig(Long companyId, DataProtection.RegulationType regulationType, 
                                                    String dataControllerName, String dataControllerEmail) {
        DataProtection dataProtection = new DataProtection();
        dataProtection.setCompanyId(companyId);
        dataProtection.setRegulationType(regulationType);
        dataProtection.setDataControllerName(dataControllerName);
        dataProtection.setDataControllerEmail(dataControllerEmail);
        
        // Configuration par défaut selon le type de réglementation
        configureDefaultSettings(dataProtection, regulationType);
        
        return dataProtection;
    }
    
    /**
     * Configure les paramètres par défaut selon le type de réglementation
     */
    private void configureDefaultSettings(DataProtection dataProtection, DataProtection.RegulationType regulationType) {
        switch (regulationType) {
            case GDPR:
                dataProtection.setConsentRequired(true);
                dataProtection.setExplicitConsentRequired(true);
                dataProtection.setDataPortabilityEnabled(true);
                dataProtection.setRightToErasureEnabled(true);
                dataProtection.setDataBreachNotificationEnabled(true);
                dataProtection.setAuditTrailEnabled(true);
                dataProtection.setEncryptionEnabled(true);
                dataProtection.setAnonymizationEnabled(true);
                dataProtection.setDataRetentionPeriodDays(2555); // 7 ans
                break;
            case CCPA:
                dataProtection.setConsentRequired(false);
                dataProtection.setExplicitConsentRequired(false);
                dataProtection.setDataPortabilityEnabled(true);
                dataProtection.setRightToErasureEnabled(true);
                dataProtection.setDataBreachNotificationEnabled(true);
                dataProtection.setAuditTrailEnabled(true);
                dataProtection.setEncryptionEnabled(true);
                dataProtection.setAnonymizationEnabled(false);
                dataProtection.setDataRetentionPeriodDays(1095); // 3 ans
                break;
            default:
                // Configuration par défaut
                dataProtection.setConsentRequired(true);
                dataProtection.setExplicitConsentRequired(false);
                dataProtection.setDataPortabilityEnabled(true);
                dataProtection.setRightToErasureEnabled(true);
                dataProtection.setDataBreachNotificationEnabled(true);
                dataProtection.setAuditTrailEnabled(true);
                dataProtection.setEncryptionEnabled(true);
                dataProtection.setAnonymizationEnabled(true);
                dataProtection.setDataRetentionPeriodDays(2555); // 7 ans
        }
    }
    
    /**
     * Enregistre un consentement
     */
    public ConsentRecord recordConsent(Long companyId, String email, ConsentRecord.ConsentType consentType, 
                                      Boolean consentGiven, ConsentRecord.ConsentMethod consentMethod, 
                                      String ipAddress, String userAgent) {
        ConsentRecord consentRecord = new ConsentRecord();
        consentRecord.setCompanyId(companyId);
        consentRecord.setEmail(email);
        consentRecord.setConsentType(consentType);
        consentRecord.setConsentGiven(consentGiven);
        consentRecord.setConsentMethod(consentMethod);
        consentRecord.setIpAddress(ipAddress);
        consentRecord.setUserAgent(userAgent);
        consentRecord.setConsentDate(LocalDateTime.now());
        
        return consentRecord;
    }
    
    /**
     * Retire un consentement
     */
    public void withdrawConsent(ConsentRecord consentRecord, ConsentRecord.ConsentMethod method, String ipAddress) {
        consentRecord.withdrawConsent(method, ipAddress);
    }
    
    /**
     * Vérifie si un consentement est actif
     */
    public boolean isConsentActive(ConsentRecord consentRecord) {
        return consentRecord != null && consentRecord.isConsentActive();
    }
    
    /**
     * Crée une activité de traitement des données
     */
    public DataProcessingActivity createProcessingActivity(Long companyId, String activityName, String purpose, 
                                                          DataProcessingActivity.LegalBasis legalBasis) {
        DataProcessingActivity activity = new DataProcessingActivity();
        activity.setCompanyId(companyId);
        activity.setActivityName(activityName);
        activity.setPurpose(purpose);
        activity.setLegalBasis(legalBasis);
        
        return activity;
    }
    
    /**
     * Vérifie la conformité RGPD
     */
    public Map<String, Object> checkGDPRCompliance(DataProtection dataProtection) {
        Map<String, Object> compliance = new HashMap<>();
        
        compliance.put("isCompliant", dataProtection.isGDPRCompliant());
        compliance.put("regulationType", dataProtection.getRegulationType());
        
        List<String> issues = new ArrayList<>();
        
        if (dataProtection.getDataControllerName() == null || dataProtection.getDataControllerName().trim().isEmpty()) {
            issues.add("Nom du responsable du traitement manquant");
        }
        
        if (dataProtection.getDataControllerEmail() == null || dataProtection.getDataControllerEmail().trim().isEmpty()) {
            issues.add("Email du responsable du traitement manquant");
        }
        
        if (dataProtection.getPrivacyPolicyUrl() == null || dataProtection.getPrivacyPolicyUrl().trim().isEmpty()) {
            issues.add("URL de la politique de confidentialité manquante");
        }
        
        if (!dataProtection.getConsentRequired()) {
            issues.add("Consentement requis non activé");
        }
        
        if (!dataProtection.getDataPortabilityEnabled()) {
            issues.add("Portabilité des données non activée");
        }
        
        if (!dataProtection.getRightToErasureEnabled()) {
            issues.add("Droit à l'effacement non activé");
        }
        
        if (!dataProtection.getDataBreachNotificationEnabled()) {
            issues.add("Notification de violation de données non activée");
        }
        
        if (!dataProtection.getAuditTrailEnabled()) {
            issues.add("Traçabilité des audits non activée");
        }
        
        if (!dataProtection.getEncryptionEnabled()) {
            issues.add("Chiffrement non activé");
        }
        
        compliance.put("issues", issues);
        compliance.put("complianceScore", calculateComplianceScore(issues));
        
        return compliance;
    }
    
    /**
     * Vérifie la conformité CCPA
     */
    public Map<String, Object> checkCCPACompliance(DataProtection dataProtection) {
        Map<String, Object> compliance = new HashMap<>();
        
        compliance.put("isCompliant", dataProtection.isCCPACompliant());
        compliance.put("regulationType", dataProtection.getRegulationType());
        
        List<String> issues = new ArrayList<>();
        
        if (dataProtection.getDataControllerName() == null || dataProtection.getDataControllerName().trim().isEmpty()) {
            issues.add("Nom de l'entreprise manquant");
        }
        
        if (dataProtection.getDataControllerEmail() == null || dataProtection.getDataControllerEmail().trim().isEmpty()) {
            issues.add("Email de contact manquant");
        }
        
        if (dataProtection.getPrivacyPolicyUrl() == null || dataProtection.getPrivacyPolicyUrl().trim().isEmpty()) {
            issues.add("URL de la politique de confidentialité manquante");
        }
        
        if (!dataProtection.getDataPortabilityEnabled()) {
            issues.add("Portabilité des données non activée");
        }
        
        if (!dataProtection.getRightToErasureEnabled()) {
            issues.add("Droit à l'effacement non activé");
        }
        
        if (!dataProtection.getAuditTrailEnabled()) {
            issues.add("Traçabilité des audits non activée");
        }
        
        compliance.put("issues", issues);
        compliance.put("complianceScore", calculateComplianceScore(issues));
        
        return compliance;
    }
    
    /**
     * Calcule le score de conformité
     */
    private int calculateComplianceScore(List<String> issues) {
        if (issues.isEmpty()) {
            return 100;
        }
        
        // Score basé sur le nombre d'issues (plus il y a d'issues, plus le score est bas)
        int maxIssues = 10; // Nombre maximum d'issues possibles
        int actualIssues = Math.min(issues.size(), maxIssues);
        
        return Math.max(0, 100 - (actualIssues * 10));
    }
    
    /**
     * Récupère les réglementations supportées
     */
    public Map<String, String> getSupportedRegulations() {
        Map<String, String> regulations = new LinkedHashMap<>();
        regulations.put("GDPR", "Règlement Général sur la Protection des Données (UE)");
        regulations.put("CCPA", "California Consumer Privacy Act (États-Unis)");
        regulations.put("PIPEDA", "Personal Information Protection and Electronic Documents Act (Canada)");
        regulations.put("LGPD", "Lei Geral de Proteção de Dados (Brésil)");
        regulations.put("PDPA", "Personal Data Protection Act (Singapour)");
        return regulations;
    }
    
    /**
     * Récupère les types de consentement supportés
     */
    public Map<String, String> getSupportedConsentTypes() {
        Map<String, String> consentTypes = new LinkedHashMap<>();
        for (ConsentRecord.ConsentType type : ConsentRecord.ConsentType.values()) {
            consentTypes.put(type.name(), type.getDescription());
        }
        return consentTypes;
    }
    
    /**
     * Récupère les bases légales supportées
     */
    public Map<String, String> getSupportedLegalBases() {
        Map<String, String> legalBases = new LinkedHashMap<>();
        for (DataProcessingActivity.LegalBasis basis : DataProcessingActivity.LegalBasis.values()) {
            legalBases.put(basis.name(), basis.getDescription());
        }
        return legalBases;
    }
}



