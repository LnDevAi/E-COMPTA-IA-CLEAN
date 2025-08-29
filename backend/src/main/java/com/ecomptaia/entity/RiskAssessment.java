package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "risk_id", nullable = false)
    private Long riskId;

    @Column(name = "assessment_date", nullable = false)
    private LocalDateTime assessmentDate;

    @Column(name = "assessor_id", nullable = false)
    private Long assessorId;

    @Column(name = "assessment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssessmentType assessmentType;

    @Column(name = "probability_score")
    private Integer probabilityScore; // 1-5

    @Column(name = "impact_score")
    private Integer impactScore; // 1-5

    @Column(name = "risk_score")
    private Integer riskScore; // Calculé: probability * impact

    @Column(name = "risk_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private Risk.RiskLevel riskLevel;

    @Column(name = "probability_percentage")
    private Double probabilityPercentage;

    @Column(name = "estimated_financial_impact")
    private BigDecimal estimatedFinancialImpact;

    @Column(name = "currency")
    private String currency;

    @Column(name = "impact_duration_days")
    private Integer impactDurationDays;

    @Column(name = "detection_difficulty")
    @Enumerated(EnumType.STRING)
    private Risk.DetectionDifficulty detectionDifficulty;

    @Column(name = "trend_direction")
    @Enumerated(EnumType.STRING)
    private Risk.TrendDirection trendDirection;

    @Column(name = "mitigation_effectiveness")
    private Integer mitigationEffectiveness; // 1-5

    @Column(name = "control_effectiveness")
    private Integer controlEffectiveness; // 1-5

    @Column(name = "residual_risk_score")
    private Integer residualRiskScore;

    @Column(name = "residual_risk_level")
    @Enumerated(EnumType.STRING)
    private Risk.RiskLevel residualRiskLevel;

    @Column(name = "assessment_notes", columnDefinition = "TEXT")
    private String assessmentNotes;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "action_items", columnDefinition = "TEXT")
    private String actionItems;

    @Column(name = "next_assessment_date")
    private LocalDateTime nextAssessmentDate;

    @Column(name = "assessment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssessmentStatus assessmentStatus;

    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approval_notes")
    private String approvalNotes;

    @Column(name = "external_factors", columnDefinition = "TEXT")
    private String externalFactors;

    @Column(name = "internal_factors", columnDefinition = "TEXT")
    private String internalFactors;

    @Column(name = "market_conditions", columnDefinition = "TEXT")
    private String marketConditions;

    @Column(name = "regulatory_changes", columnDefinition = "TEXT")
    private String regulatoryChanges;

    @Column(name = "compliance_status")
    @Enumerated(EnumType.STRING)
    private ComplianceStatus complianceStatus;

    @Column(name = "compliance_score")
    private Integer complianceScore; // 1-5

    @Column(name = "compliance_notes", columnDefinition = "TEXT")
    private String complianceNotes;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON des métadonnées

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUMS ====================

    public enum AssessmentType {
        INITIAL("Évaluation initiale"),
        PERIODIC("Évaluation périodique"),
        TRIGGERED("Évaluation déclenchée"),
        REVIEW("Révision"),
        AUDIT("Audit"),
        COMPLIANCE("Évaluation de conformité"),
        STRESS_TEST("Test de stress"),
        SCENARIO("Évaluation de scénario");

        private final String description;

        AssessmentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum AssessmentStatus {
        DRAFT("Brouillon"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminé"),
        REVIEWED("Révisé"),
        APPROVED("Approuvé"),
        REJECTED("Rejeté"),
        ARCHIVED("Archivé");

        private final String description;

        AssessmentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ApprovalStatus {
        PENDING("En attente"),
        APPROVED("Approuvé"),
        REJECTED("Rejeté"),
        CONDITIONAL("Conditionnel"),
        NOT_REQUIRED("Non requis");

        private final String description;

        ApprovalStatus(String description) {
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
        EXEMPT("Exempté");

        private final String description;

        ComplianceStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public RiskAssessment() {
        this.assessmentDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.assessmentStatus = AssessmentStatus.DRAFT;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.complianceStatus = ComplianceStatus.UNDER_REVIEW;
        this.riskLevel = Risk.RiskLevel.MEDIUM; // Valeur par défaut
        this.residualRiskLevel = Risk.RiskLevel.MEDIUM; // Valeur par défaut
    }

    public RiskAssessment(Long riskId, Long assessorId, AssessmentType assessmentType, Long entrepriseId) {
        this();
        this.riskId = riskId;
        this.assessorId = assessorId;
        this.assessmentType = assessmentType;
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
     * Calculer le score de risque résiduel
     */
    public void calculateResidualRiskScore() {
        if (this.riskScore != null && this.mitigationEffectiveness != null) {
            // Formule simplifiée: risque résiduel = risque initial * (1 - efficacité mitigation)
            double mitigationFactor = (5 - this.mitigationEffectiveness) / 5.0;
            this.residualRiskScore = (int) (this.riskScore * mitigationFactor);
            this.residualRiskLevel = determineRiskLevel(this.residualRiskScore);
        }
    }

    /**
     * Déterminer le niveau de risque basé sur le score
     */
    private Risk.RiskLevel determineRiskLevel(Integer score) {
        if (score <= 4) return Risk.RiskLevel.VERY_LOW;
        if (score <= 8) return Risk.RiskLevel.LOW;
        if (score <= 12) return Risk.RiskLevel.MEDIUM;
        if (score <= 16) return Risk.RiskLevel.HIGH;
        if (score <= 20) return Risk.RiskLevel.VERY_HIGH;
        return Risk.RiskLevel.CRITICAL;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public Long getAssessorId() {
        return assessorId;
    }

    public void setAssessorId(Long assessorId) {
        this.assessorId = assessorId;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
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

    public Risk.RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Risk.RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getProbabilityPercentage() {
        return probabilityPercentage;
    }

    public void setProbabilityPercentage(Double probabilityPercentage) {
        this.probabilityPercentage = probabilityPercentage;
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

    public Integer getImpactDurationDays() {
        return impactDurationDays;
    }

    public void setImpactDurationDays(Integer impactDurationDays) {
        this.impactDurationDays = impactDurationDays;
    }

    public Risk.DetectionDifficulty getDetectionDifficulty() {
        return detectionDifficulty;
    }

    public void setDetectionDifficulty(Risk.DetectionDifficulty detectionDifficulty) {
        this.detectionDifficulty = detectionDifficulty;
    }

    public Risk.TrendDirection getTrendDirection() {
        return trendDirection;
    }

    public void setTrendDirection(Risk.TrendDirection trendDirection) {
        this.trendDirection = trendDirection;
    }

    public Integer getMitigationEffectiveness() {
        return mitigationEffectiveness;
    }

    public void setMitigationEffectiveness(Integer mitigationEffectiveness) {
        this.mitigationEffectiveness = mitigationEffectiveness;
        calculateResidualRiskScore();
    }

    public Integer getControlEffectiveness() {
        return controlEffectiveness;
    }

    public void setControlEffectiveness(Integer controlEffectiveness) {
        this.controlEffectiveness = controlEffectiveness;
    }

    public Integer getResidualRiskScore() {
        return residualRiskScore;
    }

    public void setResidualRiskScore(Integer residualRiskScore) {
        this.residualRiskScore = residualRiskScore;
    }

    public Risk.RiskLevel getResidualRiskLevel() {
        return residualRiskLevel;
    }

    public void setResidualRiskLevel(Risk.RiskLevel residualRiskLevel) {
        this.residualRiskLevel = residualRiskLevel;
    }

    public String getAssessmentNotes() {
        return assessmentNotes;
    }

    public void setAssessmentNotes(String assessmentNotes) {
        this.assessmentNotes = assessmentNotes;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getActionItems() {
        return actionItems;
    }

    public void setActionItems(String actionItems) {
        this.actionItems = actionItems;
    }

    public LocalDateTime getNextAssessmentDate() {
        return nextAssessmentDate;
    }

    public void setNextAssessmentDate(LocalDateTime nextAssessmentDate) {
        this.nextAssessmentDate = nextAssessmentDate;
    }

    public AssessmentStatus getAssessmentStatus() {
        return assessmentStatus;
    }

    public void setAssessmentStatus(AssessmentStatus assessmentStatus) {
        this.assessmentStatus = assessmentStatus;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApprovalNotes() {
        return approvalNotes;
    }

    public void setApprovalNotes(String approvalNotes) {
        this.approvalNotes = approvalNotes;
    }

    public String getExternalFactors() {
        return externalFactors;
    }

    public void setExternalFactors(String externalFactors) {
        this.externalFactors = externalFactors;
    }

    public String getInternalFactors() {
        return internalFactors;
    }

    public void setInternalFactors(String internalFactors) {
        this.internalFactors = internalFactors;
    }

    public String getMarketConditions() {
        return marketConditions;
    }

    public void setMarketConditions(String marketConditions) {
        this.marketConditions = marketConditions;
    }

    public String getRegulatoryChanges() {
        return regulatoryChanges;
    }

    public void setRegulatoryChanges(String regulatoryChanges) {
        this.regulatoryChanges = regulatoryChanges;
    }

    public ComplianceStatus getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(ComplianceStatus complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public Integer getComplianceScore() {
        return complianceScore;
    }

    public void setComplianceScore(Integer complianceScore) {
        this.complianceScore = complianceScore;
    }

    public String getComplianceNotes() {
        return complianceNotes;
    }

    public void setComplianceNotes(String complianceNotes) {
        this.complianceNotes = complianceNotes;
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
