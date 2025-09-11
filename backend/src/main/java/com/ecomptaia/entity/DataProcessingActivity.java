package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * EntitÃ© pour les activitÃ©s de traitement des donnÃ©es (RGPD/CCPA)
 */
@Entity
@Table(name = "data_processing_activities")
public class DataProcessingActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(name = "activity_name", nullable = false)
    private String activityName;
    
    @Column(name = "activity_description", columnDefinition = "TEXT")
    private String activityDescription;
    
    @Column(name = "purpose", nullable = false, columnDefinition = "TEXT")
    private String purpose;
    
    @Column(name = "legal_basis", nullable = false)
    @Enumerated(EnumType.STRING)
    private LegalBasis legalBasis;
    
    @Column(name = "data_categories", columnDefinition = "TEXT")
    private String dataCategories; // JSON des catÃ©gories de donnÃ©es
    
    @Column(name = "data_subjects", columnDefinition = "TEXT")
    private String dataSubjects; // JSON des catÃ©gories de personnes concernÃ©es
    
    @Column(name = "recipients", columnDefinition = "TEXT")
    private String recipients; // JSON des destinataires
    
    @Column(name = "third_country_transfers", columnDefinition = "TEXT")
    private String thirdCountryTransfers; // JSON des transferts vers pays tiers
    
    @Column(name = "retention_period")
    private String retentionPeriod;
    
    @Column(name = "security_measures", columnDefinition = "TEXT")
    private String securityMeasures; // JSON des mesures de sÃ©curitÃ©
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_protection_id")
    private DataProtection dataProtection;
    
    // Constructeurs
    public DataProcessingActivity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public DataProcessingActivity(Long companyId, String activityName, String purpose, LegalBasis legalBasis) {
        this();
        this.companyId = companyId;
        this.activityName = activityName;
        this.purpose = purpose;
        this.legalBasis = legalBasis;
    }
    
    // Ã‰numÃ©rations
    public enum LegalBasis {
        CONSENT("Consentement", "Consentement de la personne concernÃ©e"),
        CONTRACT("Contrat", "ExÃ©cution d'un contrat"),
        LEGAL_OBLIGATION("Obligation lÃ©gale", "Respect d'une obligation lÃ©gale"),
        VITAL_INTERESTS("IntÃ©rÃªts vitaux", "Protection des intÃ©rÃªts vitaux"),
        PUBLIC_TASK("Mission d'intÃ©rÃªt public", "ExÃ©cution d'une mission d'intÃ©rÃªt public"),
        LEGITIMATE_INTERESTS("IntÃ©rÃªts lÃ©gitimes", "IntÃ©rÃªts lÃ©gitimes du responsable du traitement");
        
        private final String code;
        private final String description;
        
        LegalBasis(String code, String description) {
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
    
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    
    public String getActivityDescription() { return activityDescription; }
    public void setActivityDescription(String activityDescription) { this.activityDescription = activityDescription; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public LegalBasis getLegalBasis() { return legalBasis; }
    public void setLegalBasis(LegalBasis legalBasis) { this.legalBasis = legalBasis; }
    
    public String getDataCategories() { return dataCategories; }
    public void setDataCategories(String dataCategories) { this.dataCategories = dataCategories; }
    
    public String getDataSubjects() { return dataSubjects; }
    public void setDataSubjects(String dataSubjects) { this.dataSubjects = dataSubjects; }
    
    public String getRecipients() { return recipients; }
    public void setRecipients(String recipients) { this.recipients = recipients; }
    
    public String getThirdCountryTransfers() { return thirdCountryTransfers; }
    public void setThirdCountryTransfers(String thirdCountryTransfers) { this.thirdCountryTransfers = thirdCountryTransfers; }
    
    public String getRetentionPeriod() { return retentionPeriod; }
    public void setRetentionPeriod(String retentionPeriod) { this.retentionPeriod = retentionPeriod; }
    
    public String getSecurityMeasures() { return securityMeasures; }
    public void setSecurityMeasures(String securityMeasures) { this.securityMeasures = securityMeasures; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public DataProtection getDataProtection() { return dataProtection; }
    public void setDataProtection(DataProtection dataProtection) { this.dataProtection = dataProtection; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}





