package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workflows")
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_name", nullable = false)
    private String workflowName;

    @Column(name = "workflow_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkflowType workflowType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "max_approval_levels")
    private Integer maxApprovalLevels;

    @Column(name = "auto_escalation_enabled")
    private Boolean autoEscalationEnabled;

    @Column(name = "escalation_delay_hours")
    private Integer escalationDelayHours;

    @Column(name = "requires_final_approval")
    private Boolean requiresFinalApproval;

    @Column(name = "workflow_config", columnDefinition = "TEXT")
    private String workflowConfig; // JSON configuration

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUMS ====================

    public enum WorkflowType {
        ACCOUNTING_ENTRY("Approbation d'écriture comptable"),
        DOCUMENT_VALIDATION("Validation de document"),
        PAYMENT_APPROVAL("Approbation de paiement"),
        REPORT_VALIDATION("Validation de rapport"),
        DECLARATION_APPROVAL("Approbation de déclaration"),
        EXPENSE_APPROVAL("Approbation de dépense"),
        INVOICE_APPROVAL("Approbation de facture"),
        BUDGET_APPROVAL("Approbation de budget"),
        CONTRACT_APPROVAL("Approbation de contrat"),
        CUSTOM("Workflow personnalisé");

        private final String description;

        WorkflowType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public Workflow() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.autoEscalationEnabled = false;
        this.requiresFinalApproval = true;
        this.maxApprovalLevels = 3;
        this.escalationDelayHours = 24;
    }

    public Workflow(String workflowName, WorkflowType workflowType, Long entrepriseId) {
        this();
        this.workflowName = workflowName;
        this.workflowType = workflowType;
        this.entrepriseId = entrepriseId;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getMaxApprovalLevels() {
        return maxApprovalLevels;
    }

    public void setMaxApprovalLevels(Integer maxApprovalLevels) {
        this.maxApprovalLevels = maxApprovalLevels;
    }

    public Boolean getAutoEscalationEnabled() {
        return autoEscalationEnabled;
    }

    public void setAutoEscalationEnabled(Boolean autoEscalationEnabled) {
        this.autoEscalationEnabled = autoEscalationEnabled;
    }

    public Integer getEscalationDelayHours() {
        return escalationDelayHours;
    }

    public void setEscalationDelayHours(Integer escalationDelayHours) {
        this.escalationDelayHours = escalationDelayHours;
    }

    public Boolean getRequiresFinalApproval() {
        return requiresFinalApproval;
    }

    public void setRequiresFinalApproval(Boolean requiresFinalApproval) {
        this.requiresFinalApproval = requiresFinalApproval;
    }

    public String getWorkflowConfig() {
        return workflowConfig;
    }

    public void setWorkflowConfig(String workflowConfig) {
        this.workflowConfig = workflowConfig;
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




