package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_approvals")
public class WorkflowApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_instance_id", nullable = false)
    private Long workflowInstanceId;

    @Column(name = "approver_id", nullable = false)
    private Long approverId;

    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel;

    @Column(name = "approval_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "approval_reason")
    private String approvalReason;

    @Column(name = "is_delegated")
    private Boolean isDelegated;

    @Column(name = "delegated_to")
    private Long delegatedTo;

    @Column(name = "delegation_reason")
    private String delegationReason;

    @Column(name = "notification_sent")
    private Boolean notificationSent;

    @Column(name = "notification_date")
    private LocalDateTime notificationDate;

    @Column(name = "reminder_sent")
    private Boolean reminderSent;

    @Column(name = "reminder_date")
    private LocalDateTime reminderDate;

    @Column(name = "escalation_triggered")
    private Boolean escalationTriggered;

    @Column(name = "escalation_date")
    private LocalDateTime escalationDate;

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUMS ====================

    public enum ApprovalStatus {
        PENDING("En attente"),
        APPROVED("Approuvé"),
        REJECTED("Rejeté"),
        DELEGATED("Délégué"),
        ESCALATED("Escaladé"),
        CANCELLED("Annulé");

        private final String description;

        ApprovalStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public WorkflowApproval() {
        this.createdAt = LocalDateTime.now();
        this.approvalStatus = ApprovalStatus.PENDING;
        this.notificationSent = false;
        this.reminderSent = false;
        this.escalationTriggered = false;
        this.isDelegated = false;
    }

    public WorkflowApproval(Long workflowInstanceId, Long approverId, Integer approvalLevel, Long entrepriseId) {
        this();
        this.workflowInstanceId = workflowInstanceId;
        this.approverId = approverId;
        this.approvalLevel = approvalLevel;
        this.entrepriseId = entrepriseId;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    public void setWorkflowInstanceId(Long workflowInstanceId) {
        this.workflowInstanceId = workflowInstanceId;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public Boolean getIsDelegated() {
        return isDelegated;
    }

    public void setIsDelegated(Boolean isDelegated) {
        this.isDelegated = isDelegated;
    }

    public Long getDelegatedTo() {
        return delegatedTo;
    }

    public void setDelegatedTo(Long delegatedTo) {
        this.delegatedTo = delegatedTo;
    }

    public String getDelegationReason() {
        return delegationReason;
    }

    public void setDelegationReason(String delegationReason) {
        this.delegationReason = delegationReason;
    }

    public Boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    public Boolean getReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(Boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public LocalDateTime getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }

    public Boolean getEscalationTriggered() {
        return escalationTriggered;
    }

    public void setEscalationTriggered(Boolean escalationTriggered) {
        this.escalationTriggered = escalationTriggered;
    }

    public LocalDateTime getEscalationDate() {
        return escalationDate;
    }

    public void setEscalationDate(LocalDateTime escalationDate) {
        this.escalationDate = escalationDate;
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







