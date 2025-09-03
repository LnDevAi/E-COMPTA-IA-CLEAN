package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "risks")
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "risk_name", nullable = false)
    private String riskName;

    @Column(name = "risk_description", columnDefinition = "TEXT")
    private String riskDescription;

    @Column(name = "risk_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiskCategory riskCategory;

    @Column(name = "risk_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiskType riskType;

    @Column(name = "probability_score")
    private Integer probabilityScore; // 1-5

    @Column(name = "impact_score")
    private Integer impactScore; // 1-5

    @Column(name = "risk_score")
    private Integer riskScore; // Calculé: probability * impact

    @Column(name = "risk_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(name = "estimated_financial_impact")
    private BigDecimal estimatedFinancialImpact;

    @Column(name = "currency")
    private String currency;

    @Column(name = "probability_percentage")
    private Double probabilityPercentage;

    @Column(name = "impact_duration_days")
    private Integer impactDurationDays;

    @Column(name = "detection_difficulty")
    @Enumerated(EnumType.STRING)
    private DetectionDifficulty detectionDifficulty;

    @Column(name = "mitigation_status")
    @Enumerated(EnumType.STRING)
    private MitigationStatus mitigationStatus;

    @Column(name = "mitigation_plan", columnDefinition = "TEXT")
    private String mitigationPlan;

    @Column(name = "contingency_plan", columnDefinition = "TEXT")
    private String contingencyPlan;

    @Column(name = "responsible_person")
    private String responsiblePerson;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "last_assessment_date")
    private LocalDateTime lastAssessmentDate;

    @Column(name = "next_assessment_date")
    private LocalDateTime nextAssessmentDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "requires_immediate_action")
    private Boolean requiresImmediateAction;

    @Column(name = "alert_threshold")
    private Integer alertThreshold;

    @Column(name = "compliance_requirements", columnDefinition = "TEXT")
    private String complianceRequirements;

    @Column(name = "internal_controls", columnDefinition = "TEXT")
    private String internalControls;

    @Column(name = "external_factors", columnDefinition = "TEXT")
    private String externalFactors;

    @Column(name = "historical_occurrences")
    private Integer historicalOccurrences;

    @Column(name = "last_occurrence_date")
    private LocalDateTime lastOccurrenceDate;

    @Column(name = "trend_direction")
    @Enumerated(EnumType.STRING)
    private TrendDirection trendDirection;

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

    public enum RiskCategory {
        FINANCIAL("Risque financier"),
        OPERATIONAL("Risque opérationnel"),
        COMPLIANCE("Risque de conformité"),
        TECHNOLOGICAL("Risque technologique"),
        MARKET("Risque de marché"),
        STRATEGIC("Risque stratégique"),
        REPUTATIONAL("Risque de réputation"),
        LEGAL("Risque juridique"),
        ENVIRONMENTAL("Risque environnemental"),
        SUPPLY_CHAIN("Risque de chaîne d'approvisionnement");

        private final String description;

        RiskCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum RiskType {
        CREDIT("Risque de crédit"),
        LIQUIDITY("Risque de liquidité"),
        INTEREST_RATE("Risque de taux d'intérêt"),
        EXCHANGE_RATE("Risque de change"),
        FRAUD("Risque de fraude"),
        CYBER("Risque cyber"),
        REGULATORY("Risque réglementaire"),
        OPERATIONAL_FAILURE("Défaillance opérationnelle"),
        HUMAN_ERROR("Erreur humaine"),
        NATURAL_DISASTER("Catastrophe naturelle"),
        POLITICAL("Risque politique"),
        ECONOMIC("Risque économique"),
        COMPETITION("Risque concurrentiel"),
        SUPPLIER("Risque fournisseur"),
        CUSTOMER("Risque client");

        private final String description;

        RiskType(String description) {
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

    public enum DetectionDifficulty {
        VERY_EASY("Très facile"),
        EASY("Facile"),
        MODERATE("Modéré"),
        DIFFICULT("Difficile"),
        VERY_DIFFICULT("Très difficile");

        private final String description;

        DetectionDifficulty(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum MitigationStatus {
        NOT_STARTED("Non commencé"),
        IN_PROGRESS("En cours"),
        PARTIALLY_COMPLETED("Partiellement terminé"),
        COMPLETED("Terminé"),
        MONITORING("En surveillance"),
        REVIEW_REQUIRED("Révision requise");

        private final String description;

        MitigationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum TrendDirection {
        DECREASING("Diminuant"),
        STABLE("Stable"),
        INCREASING("Augmentant"),
        VOLATILE("Volatile");

        private final String description;

        TrendDirection(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public Risk() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.requiresImmediateAction = false;
        this.historicalOccurrences = 0;
        this.trendDirection = TrendDirection.STABLE;
        this.mitigationStatus = MitigationStatus.NOT_STARTED;
        this.riskLevel = RiskLevel.MEDIUM;
    }

    public Risk(String riskName, RiskCategory riskCategory, RiskType riskType, Long entrepriseId) {
        this();
        this.riskName = riskName;
        this.riskCategory = riskCategory;
        this.riskType = riskType;
        this.entrepriseId = entrepriseId;
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Calculer le score de risque
     */
    public void calculateRiskScore() {
        if (this.probabilityScore != null && this.impactScore != null) {
            this.riskScore = this.probabilityScore * this.impactScore;
            this.riskLevel = determineRiskLevel(this.riskScore);
        }
    }

    /**
     * Déterminer le niveau de risque basé sur le score
     */
    private RiskLevel determineRiskLevel(Integer score) {
        if (score <= 4) return RiskLevel.VERY_LOW;
        if (score <= 8) return RiskLevel.LOW;
        if (score <= 12) return RiskLevel.MEDIUM;
        if (score <= 16) return RiskLevel.HIGH;
        if (score <= 20) return RiskLevel.VERY_HIGH;
        return RiskLevel.CRITICAL;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRiskName() {
        return riskName;
    }

    public void setRiskName(String riskName) {
        this.riskName = riskName;
    }

    public String getRiskDescription() {
        return riskDescription;
    }

    public void setRiskDescription(String riskDescription) {
        this.riskDescription = riskDescription;
    }

    public RiskCategory getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(RiskCategory riskCategory) {
        this.riskCategory = riskCategory;
    }

    public RiskType getRiskType() {
        return riskType;
    }

    public void setRiskType(RiskType riskType) {
        this.riskType = riskType;
    }

    public Integer getProbabilityScore() {
        return probabilityScore;
    }

    public void setProbabilityScore(Integer probabilityScore) {
        this.probabilityScore = probabilityScore;
        calculateRiskScore();
    }

    public Integer getImpactScore() {
        return impactScore;
    }

    public void setImpactScore(Integer impactScore) {
        this.impactScore = impactScore;
        calculateRiskScore();
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal getEstimatedFinancialImpact() {
        return estimatedFinancialImpact;
    }

    public void setEstimatedFinancialImpact(BigDecimal estimatedFinancialImpact) {
        this.estimatedFinancialImpact = estimatedFinancialImpact;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getProbabilityPercentage() {
        return probabilityPercentage;
    }

    public void setProbabilityPercentage(Double probabilityPercentage) {
        this.probabilityPercentage = probabilityPercentage;
    }

    public Integer getImpactDurationDays() {
        return impactDurationDays;
    }

    public void setImpactDurationDays(Integer impactDurationDays) {
        this.impactDurationDays = impactDurationDays;
    }

    public DetectionDifficulty getDetectionDifficulty() {
        return detectionDifficulty;
    }

    public void setDetectionDifficulty(DetectionDifficulty detectionDifficulty) {
        this.detectionDifficulty = detectionDifficulty;
    }

    public MitigationStatus getMitigationStatus() {
        return mitigationStatus;
    }

    public void setMitigationStatus(MitigationStatus mitigationStatus) {
        this.mitigationStatus = mitigationStatus;
    }

    public String getMitigationPlan() {
        return mitigationPlan;
    }

    public void setMitigationPlan(String mitigationPlan) {
        this.mitigationPlan = mitigationPlan;
    }

    public String getContingencyPlan() {
        return contingencyPlan;
    }

    public void setContingencyPlan(String contingencyPlan) {
        this.contingencyPlan = contingencyPlan;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getLastAssessmentDate() {
        return lastAssessmentDate;
    }

    public void setLastAssessmentDate(LocalDateTime lastAssessmentDate) {
        this.lastAssessmentDate = lastAssessmentDate;
    }

    public LocalDateTime getNextAssessmentDate() {
        return nextAssessmentDate;
    }

    public void setNextAssessmentDate(LocalDateTime nextAssessmentDate) {
        this.nextAssessmentDate = nextAssessmentDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getRequiresImmediateAction() {
        return requiresImmediateAction;
    }

    public void setRequiresImmediateAction(Boolean requiresImmediateAction) {
        this.requiresImmediateAction = requiresImmediateAction;
    }

    public Integer getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(Integer alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public String getComplianceRequirements() {
        return complianceRequirements;
    }

    public void setComplianceRequirements(String complianceRequirements) {
        this.complianceRequirements = complianceRequirements;
    }

    public String getInternalControls() {
        return internalControls;
    }

    public void setInternalControls(String internalControls) {
        this.internalControls = internalControls;
    }

    public String getExternalFactors() {
        return externalFactors;
    }

    public void setExternalFactors(String externalFactors) {
        this.externalFactors = externalFactors;
    }

    public Integer getHistoricalOccurrences() {
        return historicalOccurrences;
    }

    public void setHistoricalOccurrences(Integer historicalOccurrences) {
        this.historicalOccurrences = historicalOccurrences;
    }

    public LocalDateTime getLastOccurrenceDate() {
        return lastOccurrenceDate;
    }

    public void setLastOccurrenceDate(LocalDateTime lastOccurrenceDate) {
        this.lastOccurrenceDate = lastOccurrenceDate;
    }

    public TrendDirection getTrendDirection() {
        return trendDirection;
    }

    public void setTrendDirection(TrendDirection trendDirection) {
        this.trendDirection = trendDirection;
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





