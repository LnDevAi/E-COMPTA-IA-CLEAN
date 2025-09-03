package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "compliance_requirements")
public class ComplianceRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requirement_code", nullable = false, unique = true)
    private String requirementCode;

    @Column(name = "requirement_name", nullable = false)
    private String requirementName;

    @Column(name = "requirement_description", columnDefinition = "TEXT")
    private String requirementDescription;

    @Column(name = "requirement_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequirementCategory requirementCategory;

    @Column(name = "requirement_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequirementType requirementType;

    @Column(name = "priority_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    @Column(name = "compliance_frequency")
    @Enumerated(EnumType.STRING)
    private ComplianceFrequency complianceFrequency;

    @Column(name = "last_compliance_date")
    private LocalDateTime lastComplianceDate;

    @Column(name = "next_compliance_date")
    private LocalDateTime nextComplianceDate;

    @Column(name = "compliance_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ComplianceStatus complianceStatus;

    @Column(name = "compliance_score")
    private Integer complianceScore; // 1-5

    @Column(name = "risk_level")
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(name = "penalty_amount")
    private BigDecimal penaltyAmount;

    @Column(name = "penalty_currency")
    private String penaltyCurrency;

    @Column(name = "regulatory_source")
    private String regulatorySource;

    @Column(name = "regulatory_reference")
    private String regulatoryReference;

    @Column(name = "implementation_notes", columnDefinition = "TEXT")
    private String implementationNotes;

    @Column(name = "control_procedures", columnDefinition = "TEXT")
    private String controlProcedures;

    @Column(name = "responsible_person")
    private String responsiblePerson;

    @Column(name = "review_frequency")
    @Enumerated(EnumType.STRING)
    private ReviewFrequency reviewFrequency;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "requires_documentation")
    private Boolean requiresDocumentation;

    @Column(name = "documentation_requirements", columnDefinition = "TEXT")
    private String documentationRequirements;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON des métadonnées

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUMS ====================

    public enum RequirementCategory {
        ACCOUNTING_STANDARDS("Normes comptables"),
        FINANCIAL_REPORTING("Rapports financiers"),
        TAX_COMPLIANCE("Conformité fiscale"),
        SOCIAL_COMPLIANCE("Conformité sociale"),
        CORPORATE_GOVERNANCE("Gouvernance d'entreprise"),
        INTERNAL_CONTROLS("Contrôles internes"),
        AUDIT_REQUIREMENTS("Exigences d'audit"),
        DATA_PROTECTION("Protection des données"),
        ENVIRONMENTAL_COMPLIANCE("Conformité environnementale"),
        OPERATIONAL_COMPLIANCE("Conformité opérationnelle");

        private final String description;

        RequirementCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum RequirementType {
        MANDATORY("Obligatoire"),
        RECOMMENDED("Recommandé"),
        OPTIONAL("Optionnel"),
        CONDITIONAL("Conditionnel");

        private final String description;

        RequirementType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum PriorityLevel {
        CRITICAL("Critique"),
        HIGH("Élevé"),
        MEDIUM("Moyen"),
        LOW("Faible");

        private final String description;

        PriorityLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ComplianceFrequency {
        DAILY("Quotidien"),
        WEEKLY("Hebdomadaire"),
        MONTHLY("Mensuel"),
        QUARTERLY("Trimestriel"),
        SEMI_ANNUAL("Semestriel"),
        ANNUAL("Annuel"),
        BIENNIAL("Biennal"),
        ON_DEMAND("Sur demande");

        private final String description;

        ComplianceFrequency(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ComplianceStatus {
        COMPLIANT("Conforme"),
        NON_COMPLIANT("Non conforme"),
        PARTIALLY_COMPLIANT("Partiellement conforme"),
        UNDER_REVIEW("En cours de révision"),
        PENDING_VERIFICATION("En attente de vérification"),
        EXEMPT("Exempté");

        private final String description;

        ComplianceStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum RiskLevel {
        VERY_LOW("Très faible"),
        LOW("Faible"),
        MEDIUM("Moyen"),
        HIGH("Élevé"),
        VERY_HIGH("Très élevé"),
        CRITICAL("Critique");

        private final String description;

        RiskLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ReviewFrequency {
        CONTINUOUS("Continu"),
        DAILY("Quotidien"),
        WEEKLY("Hebdomadaire"),
        MONTHLY("Mensuel"),
        QUARTERLY("Trimestriel"),
        SEMI_ANNUAL("Semestriel"),
        ANNUAL("Annuel");

        private final String description;

        ReviewFrequency(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public ComplianceRequirement() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.complianceStatus = ComplianceStatus.UNDER_REVIEW;
        this.requiresDocumentation = false;
    }

    public ComplianceRequirement(String requirementCode, String requirementName, 
                               RequirementCategory requirementCategory, RequirementType requirementType,
                               PriorityLevel priorityLevel, Long entrepriseId) {
        this();
        this.requirementCode = requirementCode;
        this.requirementName = requirementName;
        this.requirementCategory = requirementCategory;
        this.requirementType = requirementType;
        this.priorityLevel = priorityLevel;
        this.entrepriseId = entrepriseId;
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Calculer le score de conformité
     */
    public void calculateComplianceScore() {
        // Logique de calcul basée sur les critères de conformité
        if (this.complianceStatus == ComplianceStatus.COMPLIANT) {
            this.complianceScore = 5;
        } else if (this.complianceStatus == ComplianceStatus.PARTIALLY_COMPLIANT) {
            this.complianceScore = 3;
        } else if (this.complianceStatus == ComplianceStatus.NON_COMPLIANT) {
            this.complianceScore = 1;
        } else {
            this.complianceScore = 2;
        }
    }

    /**
     * Déterminer le niveau de risque basé sur la priorité et le statut
     */
    public void calculateRiskLevel() {
        if (this.priorityLevel == PriorityLevel.CRITICAL && 
            this.complianceStatus == ComplianceStatus.NON_COMPLIANT) {
            this.riskLevel = RiskLevel.CRITICAL;
        } else if (this.priorityLevel == PriorityLevel.HIGH && 
                   this.complianceStatus == ComplianceStatus.NON_COMPLIANT) {
            this.riskLevel = RiskLevel.VERY_HIGH;
        } else if (this.priorityLevel == PriorityLevel.MEDIUM && 
                   this.complianceStatus == ComplianceStatus.NON_COMPLIANT) {
            this.riskLevel = RiskLevel.HIGH;
        } else if (this.priorityLevel == PriorityLevel.LOW && 
                   this.complianceStatus == ComplianceStatus.NON_COMPLIANT) {
            this.riskLevel = RiskLevel.MEDIUM;
        } else {
            this.riskLevel = RiskLevel.LOW;
        }
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequirementCode() {
        return requirementCode;
    }

    public void setRequirementCode(String requirementCode) {
        this.requirementCode = requirementCode;
    }

    public String getRequirementName() {
        return requirementName;
    }

    public void setRequirementName(String requirementName) {
        this.requirementName = requirementName;
    }

    public String getRequirementDescription() {
        return requirementDescription;
    }

    public void setRequirementDescription(String requirementDescription) {
        this.requirementDescription = requirementDescription;
    }

    public RequirementCategory getRequirementCategory() {
        return requirementCategory;
    }

    public void setRequirementCategory(RequirementCategory requirementCategory) {
        this.requirementCategory = requirementCategory;
    }

    public RequirementType getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(RequirementType requirementType) {
        this.requirementType = requirementType;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public ComplianceFrequency getComplianceFrequency() {
        return complianceFrequency;
    }

    public void setComplianceFrequency(ComplianceFrequency complianceFrequency) {
        this.complianceFrequency = complianceFrequency;
    }

    public LocalDateTime getLastComplianceDate() {
        return lastComplianceDate;
    }

    public void setLastComplianceDate(LocalDateTime lastComplianceDate) {
        this.lastComplianceDate = lastComplianceDate;
    }

    public LocalDateTime getNextComplianceDate() {
        return nextComplianceDate;
    }

    public void setNextComplianceDate(LocalDateTime nextComplianceDate) {
        this.nextComplianceDate = nextComplianceDate;
    }

    public ComplianceStatus getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(ComplianceStatus complianceStatus) {
        this.complianceStatus = complianceStatus;
        calculateComplianceScore();
        calculateRiskLevel();
    }

    public Integer getComplianceScore() {
        return complianceScore;
    }

    public void setComplianceScore(Integer complianceScore) {
        this.complianceScore = complianceScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public String getPenaltyCurrency() {
        return penaltyCurrency;
    }

    public void setPenaltyCurrency(String penaltyCurrency) {
        this.penaltyCurrency = penaltyCurrency;
    }

    public String getRegulatorySource() {
        return regulatorySource;
    }

    public void setRegulatorySource(String regulatorySource) {
        this.regulatorySource = regulatorySource;
    }

    public String getRegulatoryReference() {
        return regulatoryReference;
    }

    public void setRegulatoryReference(String regulatoryReference) {
        this.regulatoryReference = regulatoryReference;
    }

    public String getImplementationNotes() {
        return implementationNotes;
    }

    public void setImplementationNotes(String implementationNotes) {
        this.implementationNotes = implementationNotes;
    }

    public String getControlProcedures() {
        return controlProcedures;
    }

    public void setControlProcedures(String controlProcedures) {
        this.controlProcedures = controlProcedures;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public ReviewFrequency getReviewFrequency() {
        return reviewFrequency;
    }

    public void setReviewFrequency(ReviewFrequency reviewFrequency) {
        this.reviewFrequency = reviewFrequency;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getRequiresDocumentation() {
        return requiresDocumentation;
    }

    public void setRequiresDocumentation(Boolean requiresDocumentation) {
        this.requiresDocumentation = requiresDocumentation;
    }

    public String getDocumentationRequirements() {
        return documentationRequirements;
    }

    public void setDocumentationRequirements(String documentationRequirements) {
        this.documentationRequirements = documentationRequirements;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}





