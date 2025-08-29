package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "audits")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "audit_code", nullable = false, unique = true)
    private String auditCode;

    @Column(name = "audit_name", nullable = false)
    private String auditName;

    @Column(name = "audit_description", columnDefinition = "TEXT")
    private String auditDescription;

    @Column(name = "audit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditType auditType;

    @Column(name = "audit_scope", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditScope auditScope;

    @Column(name = "audit_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditStatus auditStatus;

    @Column(name = "audit_priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditPriority auditPriority;

    @Column(name = "planned_start_date")
    private LocalDateTime plannedStartDate;

    @Column(name = "planned_end_date")
    private LocalDateTime plannedEndDate;

    @Column(name = "actual_start_date")
    private LocalDateTime actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;

    @Column(name = "auditor_id", nullable = false)
    private Long auditorId;

    @Column(name = "audit_team", columnDefinition = "TEXT")
    private String auditTeam; // JSON des membres de l'équipe

    @Column(name = "audit_objectives", columnDefinition = "TEXT")
    private String auditObjectives;

    @Column(name = "audit_criteria", columnDefinition = "TEXT")
    private String auditCriteria;

    @Column(name = "audit_methodology", columnDefinition = "TEXT")
    private String auditMethodology;

    @Column(name = "risk_assessment", columnDefinition = "TEXT")
    private String riskAssessment;

    @Column(name = "audit_findings", columnDefinition = "TEXT")
    private String auditFindings;

    @Column(name = "audit_conclusions", columnDefinition = "TEXT")
    private String auditConclusions;

    @Column(name = "audit_recommendations", columnDefinition = "TEXT")
    private String auditRecommendations;

    @Column(name = "audit_score")
    private Integer auditScore; // 1-5

    @Column(name = "compliance_score")
    private Integer complianceScore; // 1-5

    @Column(name = "risk_score")
    private Integer riskScore; // 1-5

    @Column(name = "overall_rating")
    @Enumerated(EnumType.STRING)
    private OverallRating overallRating;

    @Column(name = "budget_amount")
    private BigDecimal budgetAmount;

    @Column(name = "budget_currency")
    private String budgetCurrency;

    @Column(name = "actual_cost")
    private BigDecimal actualCost;

    @Column(name = "cost_currency")
    private String costCurrency;

    @Column(name = "external_auditor")
    private String externalAuditor;

    @Column(name = "regulatory_requirements", columnDefinition = "TEXT")
    private String regulatoryRequirements;

    @Column(name = "compliance_issues", columnDefinition = "TEXT")
    private String complianceIssues;

    @Column(name = "corrective_actions", columnDefinition = "TEXT")
    private String correctiveActions;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approval_notes")
    private String approvalNotes;

    @Column(name = "report_generated")
    private Boolean reportGenerated;

    @Column(name = "report_path")
    private String reportPath;

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

    public enum AuditType {
        INTERNAL("Audit interne"),
        EXTERNAL("Audit externe"),
        REGULATORY("Audit réglementaire"),
        COMPLIANCE("Audit de conformité"),
        FINANCIAL("Audit financier"),
        OPERATIONAL("Audit opérationnel"),
        IT_AUDIT("Audit informatique"),
        ENVIRONMENTAL("Audit environnemental"),
        SOCIAL("Audit social"),
        INTEGRATED("Audit intégré");

        private final String description;

        AuditType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum AuditScope {
        FULL("Audit complet"),
        LIMITED("Audit limité"),
        SPECIFIC("Audit spécifique"),
        SAMPLE("Audit par échantillonnage"),
        FOCUSED("Audit ciblé");

        private final String description;

        AuditScope(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum AuditStatus {
        PLANNED("Planifié"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminé"),
        ON_HOLD("En attente"),
        CANCELLED("Annulé"),
        SUSPENDED("Suspendu"),
        UNDER_REVIEW("En cours de révision"),
        APPROVED("Approuvé"),
        REJECTED("Rejeté");

        private final String description;

        AuditStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum AuditPriority {
        CRITICAL("Critique"),
        HIGH("Élevé"),
        MEDIUM("Moyen"),
        LOW("Faible");

        private final String description;

        AuditPriority(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum OverallRating {
        EXCELLENT("Excellent"),
        GOOD("Bon"),
        SATISFACTORY("Satisfaisant"),
        NEEDS_IMPROVEMENT("Nécessite des améliorations"),
        UNSATISFACTORY("Insatisfaisant"),
        CRITICAL("Critique");

        private final String description;

        OverallRating(String description) {
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

    // ==================== CONSTRUCTEURS ====================

    public Audit() {
        this.createdAt = LocalDateTime.now();
        this.auditStatus = AuditStatus.PLANNED;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.followUpRequired = false;
        this.reportGenerated = false;
    }

    public Audit(String auditCode, String auditName, AuditType auditType, 
                AuditScope auditScope, AuditPriority auditPriority, 
                Long auditorId, Long entrepriseId) {
        this();
        this.auditCode = auditCode;
        this.auditName = auditName;
        this.auditType = auditType;
        this.auditScope = auditScope;
        this.auditPriority = auditPriority;
        this.auditorId = auditorId;
        this.entrepriseId = entrepriseId;
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Calculer le score global de l'audit
     */
    public void calculateOverallScore() {
        if (this.auditScore != null && this.complianceScore != null && this.riskScore != null) {
            double overallScore = (this.auditScore + this.complianceScore + this.riskScore) / 3.0;
            
            if (overallScore >= 4.5) {
                this.overallRating = OverallRating.EXCELLENT;
            } else if (overallScore >= 3.5) {
                this.overallRating = OverallRating.GOOD;
            } else if (overallScore >= 2.5) {
                this.overallRating = OverallRating.SATISFACTORY;
            } else if (overallScore >= 1.5) {
                this.overallRating = OverallRating.NEEDS_IMPROVEMENT;
            } else {
                this.overallRating = OverallRating.UNSATISFACTORY;
            }
        }
    }

    /**
     * Vérifier si l'audit est en retard
     */
    public boolean isOverdue() {
        return this.plannedEndDate != null && 
               LocalDateTime.now().isAfter(this.plannedEndDate) && 
               this.auditStatus != AuditStatus.COMPLETED;
    }

    /**
     * Calculer la durée de l'audit
     */
    public Long getAuditDuration() {
        if (this.actualStartDate != null && this.actualEndDate != null) {
            return java.time.Duration.between(this.actualStartDate, this.actualEndDate).toDays();
        }
        return null;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuditCode() {
        return auditCode;
    }

    public void setAuditCode(String auditCode) {
        this.auditCode = auditCode;
    }

    public String getAuditName() {
        return auditName;
    }

    public void setAuditName(String auditName) {
        this.auditName = auditName;
    }

    public String getAuditDescription() {
        return auditDescription;
    }

    public void setAuditDescription(String auditDescription) {
        this.auditDescription = auditDescription;
    }

    public AuditType getAuditType() {
        return auditType;
    }

    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }

    public AuditScope getAuditScope() {
        return auditScope;
    }

    public void setAuditScope(AuditScope auditScope) {
        this.auditScope = auditScope;
    }

    public AuditStatus getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(AuditStatus auditStatus) {
        this.auditStatus = auditStatus;
    }

    public AuditPriority getAuditPriority() {
        return auditPriority;
    }

    public void setAuditPriority(AuditPriority auditPriority) {
        this.auditPriority = auditPriority;
    }

    public LocalDateTime getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(LocalDateTime plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public LocalDateTime getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(LocalDateTime plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public LocalDateTime getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(LocalDateTime actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public LocalDateTime getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDateTime actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditTeam() {
        return auditTeam;
    }

    public void setAuditTeam(String auditTeam) {
        this.auditTeam = auditTeam;
    }

    public String getAuditObjectives() {
        return auditObjectives;
    }

    public void setAuditObjectives(String auditObjectives) {
        this.auditObjectives = auditObjectives;
    }

    public String getAuditCriteria() {
        return auditCriteria;
    }

    public void setAuditCriteria(String auditCriteria) {
        this.auditCriteria = auditCriteria;
    }

    public String getAuditMethodology() {
        return auditMethodology;
    }

    public void setAuditMethodology(String auditMethodology) {
        this.auditMethodology = auditMethodology;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public String getAuditFindings() {
        return auditFindings;
    }

    public void setAuditFindings(String auditFindings) {
        this.auditFindings = auditFindings;
    }

    public String getAuditConclusions() {
        return auditConclusions;
    }

    public void setAuditConclusions(String auditConclusions) {
        this.auditConclusions = auditConclusions;
    }

    public String getAuditRecommendations() {
        return auditRecommendations;
    }

    public void setAuditRecommendations(String auditRecommendations) {
        this.auditRecommendations = auditRecommendations;
    }

    public Integer getAuditScore() {
        return auditScore;
    }

    public void setAuditScore(Integer auditScore) {
        this.auditScore = auditScore;
        calculateOverallScore();
    }

    public Integer getComplianceScore() {
        return complianceScore;
    }

    public void setComplianceScore(Integer complianceScore) {
        this.complianceScore = complianceScore;
        calculateOverallScore();
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
        calculateOverallScore();
    }

    public OverallRating getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(OverallRating overallRating) {
        this.overallRating = overallRating;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public String getBudgetCurrency() {
        return budgetCurrency;
    }

    public void setBudgetCurrency(String budgetCurrency) {
        this.budgetCurrency = budgetCurrency;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public String getCostCurrency() {
        return costCurrency;
    }

    public void setCostCurrency(String costCurrency) {
        this.costCurrency = costCurrency;
    }

    public String getExternalAuditor() {
        return externalAuditor;
    }

    public void setExternalAuditor(String externalAuditor) {
        this.externalAuditor = externalAuditor;
    }

    public String getRegulatoryRequirements() {
        return regulatoryRequirements;
    }

    public void setRegulatoryRequirements(String regulatoryRequirements) {
        this.regulatoryRequirements = regulatoryRequirements;
    }

    public String getComplianceIssues() {
        return complianceIssues;
    }

    public void setComplianceIssues(String complianceIssues) {
        this.complianceIssues = complianceIssues;
    }

    public String getCorrectiveActions() {
        return correctiveActions;
    }

    public void setCorrectiveActions(String correctiveActions) {
        this.correctiveActions = correctiveActions;
    }

    public Boolean getFollowUpRequired() {
        return followUpRequired;
    }

    public void setFollowUpRequired(Boolean followUpRequired) {
        this.followUpRequired = followUpRequired;
    }

    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
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

    public Boolean getReportGenerated() {
        return reportGenerated;
    }

    public void setReportGenerated(Boolean reportGenerated) {
        this.reportGenerated = reportGenerated;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
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




