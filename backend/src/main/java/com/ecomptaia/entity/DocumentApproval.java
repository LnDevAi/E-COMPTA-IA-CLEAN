package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_approvals")
public class DocumentApproval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "document_id", nullable = false)
    private Long documentId;
    
    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel;
    
    @Column(name = "approver_id", nullable = false)
    private Long approverId;
    
    @Column(name = "approver_name")
    private String approverName;
    
    @Column(name = "approval_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
    
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;
    
    @Column(name = "is_required")
    private Boolean isRequired = true;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "is_overdue")
    private Boolean isOverdue = false;
    
    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;
    
    @Column(name = "reminder_count")
    private Integer reminderCount = 0;
    
    @Column(name = "last_reminder_date")
    private LocalDateTime lastReminderDate;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(name = "country_code", nullable = false)
    private String countryCode;
    
    @Column(name = "accounting_standard", nullable = false)
    private String accountingStandard;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum ApprovalStatus {
        PENDING,            // En attente
        APPROVED,           // Approuvé
        REJECTED,           // Rejeté
        CANCELLED,          // Annulé
        DELEGATED,          // Délégué
        EXPIRED             // Expiré
    }
    
    // Constructeurs
    public DocumentApproval() {
        this.createdAt = LocalDateTime.now();
        this.approvalStatus = ApprovalStatus.PENDING;
    }
    
    public DocumentApproval(Long documentId, Integer approvalLevel, Long approverId, 
                           Long companyId, String countryCode, String accountingStandard) {
        this();
        this.documentId = documentId;
        this.approvalLevel = approvalLevel;
        this.approverId = approverId;
        this.companyId = companyId;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }
    
    public Integer getApprovalLevel() { return approvalLevel; }
    public void setApprovalLevel(Integer approvalLevel) { this.approvalLevel = approvalLevel; }
    
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
    
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }
    
    public Boolean getReminderSent() { return reminderSent; }
    public void setReminderSent(Boolean reminderSent) { this.reminderSent = reminderSent; }
    
    public Integer getReminderCount() { return reminderCount; }
    public void setReminderCount(Integer reminderCount) { this.reminderCount = reminderCount; }
    
    public LocalDateTime getLastReminderDate() { return lastReminderDate; }
    public void setLastReminderDate(LocalDateTime lastReminderDate) { this.lastReminderDate = lastReminderDate; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}



