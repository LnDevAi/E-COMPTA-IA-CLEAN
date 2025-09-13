package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EntitÃ© pour la gestion de la protection des donnÃ©es (RGPD/CCPA)
 */
@Entity
@Table(name = "data_protection")
public class DataProtection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(name = "regulation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RegulationType regulationType;
    
    @Column(name = "data_controller_name", nullable = false)
    private String dataControllerName;
    
    @Column(name = "data_controller_email", nullable = false)
    private String dataControllerEmail;
    
    @Column(name = "data_controller_address", columnDefinition = "TEXT")
    private String dataControllerAddress;
    
    @Column(name = "data_protection_officer_name")
    private String dataProtectionOfficerName;
    
    @Column(name = "data_protection_officer_email")
    private String dataProtectionOfficerEmail;
    
    @Column(name = "privacy_policy_url")
    private String privacyPolicyUrl;
    
    @Column(name = "terms_of_service_url")
    private String termsOfServiceUrl;
    
    @Column(name = "cookie_policy_url")
    private String cookiePolicyUrl;
    
    @Column(name = "data_retention_period_days")
    private Integer dataRetentionPeriodDays;
    
    @Column(name = "consent_required", nullable = false)
    private Boolean consentRequired = true;
    
    @Column(name = "explicit_consent_required", nullable = false)
    private Boolean explicitConsentRequired = true;
    
    @Column(name = "data_portability_enabled", nullable = false)
    private Boolean dataPortabilityEnabled = true;
    
    @Column(name = "right_to_erasure_enabled", nullable = false)
    private Boolean rightToErasureEnabled = true;
    
    @Column(name = "data_breach_notification_enabled", nullable = false)
    private Boolean dataBreachNotificationEnabled = true;
    
    @Column(name = "audit_trail_enabled", nullable = false)
    private Boolean auditTrailEnabled = true;
    
    @Column(name = "encryption_enabled", nullable = false)
    private Boolean encryptionEnabled = true;
    
    @Column(name = "anonymization_enabled", nullable = false)
    private Boolean anonymizationEnabled = true;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_review_date")
    private LocalDateTime lastReviewDate;
    
    @Column(name = "next_review_date")
    private LocalDateTime nextReviewDate;
    
    // Relations
    @OneToMany(mappedBy = "dataProtection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DataProcessingActivity> processingActivities;
    
    @OneToMany(mappedBy = "dataProtection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConsentRecord> consentRecords;
    
    // Constructeurs
    public DataProtection() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public DataProtection(Long companyId, RegulationType regulationType, String dataControllerName, String dataControllerEmail) {
        this();
        this.companyId = companyId;
        this.regulationType = regulationType;
        this.dataControllerName = dataControllerName;
        this.dataControllerEmail = dataControllerEmail;
    }
    
    // Ã‰numÃ©rations
    public enum RegulationType {
        GDPR("RGPD", "RÃ¨glement GÃ©nÃ©ral sur la Protection des DonnÃ©es"),
        CCPA("CCPA", "California Consumer Privacy Act"),
        PIPEDA("PIPEDA", "Personal Information Protection and Electronic Documents Act"),
        LGPD("LGPD", "Lei Geral de ProteÃ§Ã£o de Dados"),
        PDPA("PDPA", "Personal Data Protection Act");
        
        private final String code;
        private final String description;
        
        RegulationType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public RegulationType getRegulationType() { return regulationType; }
    public void setRegulationType(RegulationType regulationType) { this.regulationType = regulationType; }
    
    public String getDataControllerName() { return dataControllerName; }
    public void setDataControllerName(String dataControllerName) { this.dataControllerName = dataControllerName; }
    
    public String getDataControllerEmail() { return dataControllerEmail; }
    public void setDataControllerEmail(String dataControllerEmail) { this.dataControllerEmail = dataControllerEmail; }
    
    public String getDataControllerAddress() { return dataControllerAddress; }
    public void setDataControllerAddress(String dataControllerAddress) { this.dataControllerAddress = dataControllerAddress; }
    
    public String getDataProtectionOfficerName() { return dataProtectionOfficerName; }
    public void setDataProtectionOfficerName(String dataProtectionOfficerName) { this.dataProtectionOfficerName = dataProtectionOfficerName; }
    
    public String getDataProtectionOfficerEmail() { return dataProtectionOfficerEmail; }
    public void setDataProtectionOfficerEmail(String dataProtectionOfficerEmail) { this.dataProtectionOfficerEmail = dataProtectionOfficerEmail; }
    
    public String getPrivacyPolicyUrl() { return privacyPolicyUrl; }
    public void setPrivacyPolicyUrl(String privacyPolicyUrl) { this.privacyPolicyUrl = privacyPolicyUrl; }
    
    public String getTermsOfServiceUrl() { return termsOfServiceUrl; }
    public void setTermsOfServiceUrl(String termsOfServiceUrl) { this.termsOfServiceUrl = termsOfServiceUrl; }
    
    public String getCookiePolicyUrl() { return cookiePolicyUrl; }
    public void setCookiePolicyUrl(String cookiePolicyUrl) { this.cookiePolicyUrl = cookiePolicyUrl; }
    
    public Integer getDataRetentionPeriodDays() { return dataRetentionPeriodDays; }
    public void setDataRetentionPeriodDays(Integer dataRetentionPeriodDays) { this.dataRetentionPeriodDays = dataRetentionPeriodDays; }
    
    public Boolean getConsentRequired() { return consentRequired; }
    public void setConsentRequired(Boolean consentRequired) { this.consentRequired = consentRequired; }
    
    public Boolean getExplicitConsentRequired() { return explicitConsentRequired; }
    public void setExplicitConsentRequired(Boolean explicitConsentRequired) { this.explicitConsentRequired = explicitConsentRequired; }
    
    public Boolean getDataPortabilityEnabled() { return dataPortabilityEnabled; }
    public void setDataPortabilityEnabled(Boolean dataPortabilityEnabled) { this.dataPortabilityEnabled = dataPortabilityEnabled; }
    
    public Boolean getRightToErasureEnabled() { return rightToErasureEnabled; }
    public void setRightToErasureEnabled(Boolean rightToErasureEnabled) { this.rightToErasureEnabled = rightToErasureEnabled; }
    
    public Boolean getDataBreachNotificationEnabled() { return dataBreachNotificationEnabled; }
    public void setDataBreachNotificationEnabled(Boolean dataBreachNotificationEnabled) { this.dataBreachNotificationEnabled = dataBreachNotificationEnabled; }
    
    public Boolean getAuditTrailEnabled() { return auditTrailEnabled; }
    public void setAuditTrailEnabled(Boolean auditTrailEnabled) { this.auditTrailEnabled = auditTrailEnabled; }
    
    public Boolean getEncryptionEnabled() { return encryptionEnabled; }
    public void setEncryptionEnabled(Boolean encryptionEnabled) { this.encryptionEnabled = encryptionEnabled; }
    
    public Boolean getAnonymizationEnabled() { return anonymizationEnabled; }
    public void setAnonymizationEnabled(Boolean anonymizationEnabled) { this.anonymizationEnabled = anonymizationEnabled; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastReviewDate() { return lastReviewDate; }
    public void setLastReviewDate(LocalDateTime lastReviewDate) { this.lastReviewDate = lastReviewDate; }
    
    public LocalDateTime getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(LocalDateTime nextReviewDate) { this.nextReviewDate = nextReviewDate; }
    
    public List<DataProcessingActivity> getProcessingActivities() { return processingActivities; }
    public void setProcessingActivities(List<DataProcessingActivity> processingActivities) { this.processingActivities = processingActivities; }
    
    public List<ConsentRecord> getConsentRecords() { return consentRecords; }
    public void setConsentRecords(List<ConsentRecord> consentRecords) { this.consentRecords = consentRecords; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * VÃ©rifie si la configuration est conforme au RGPD
     */
    public boolean isGDPRCompliant() {
        return regulationType == RegulationType.GDPR &&
               dataControllerName != null && !dataControllerName.trim().isEmpty() &&
               dataControllerEmail != null && !dataControllerEmail.trim().isEmpty() &&
               privacyPolicyUrl != null && !privacyPolicyUrl.trim().isEmpty() &&
               consentRequired &&
               dataPortabilityEnabled &&
               rightToErasureEnabled &&
               dataBreachNotificationEnabled &&
               auditTrailEnabled &&
               encryptionEnabled;
    }
    
    /**
     * VÃ©rifie si la configuration est conforme au CCPA
     */
    public boolean isCCPACompliant() {
        return regulationType == RegulationType.CCPA &&
               dataControllerName != null && !dataControllerName.trim().isEmpty() &&
               dataControllerEmail != null && !dataControllerEmail.trim().isEmpty() &&
               privacyPolicyUrl != null && !privacyPolicyUrl.trim().isEmpty() &&
               dataPortabilityEnabled &&
               rightToErasureEnabled &&
               auditTrailEnabled;
    }
}





