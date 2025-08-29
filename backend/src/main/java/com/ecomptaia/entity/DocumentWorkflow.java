package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_workflows")
public class DocumentWorkflow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "workflow_code", unique = true, nullable = false)
    private String workflowCode;
    
    @Column(name = "workflow_name", nullable = false)
    private String workflowName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GedDocument.DocumentType documentType;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "requires_approval")
    private Boolean requiresApproval = true;
    
    @Column(name = "approval_levels")
    private Integer approvalLevels = 1;
    
    @Column(name = "auto_approve")
    private Boolean autoApprove = false;
    
    @Column(name = "auto_archive")
    private Boolean autoArchive = false;
    
    @Column(name = "archive_days")
    private Integer archiveDays = 365;
    
    @Column(name = "retention_years")
    private Integer retentionYears = 7;
    
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
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Constructeurs
    public DocumentWorkflow() {
        this.createdAt = LocalDateTime.now();
    }
    
    public DocumentWorkflow(String workflowCode, String workflowName, GedDocument.DocumentType documentType, 
                           Long companyId, String countryCode, String accountingStandard) {
        this();
        this.workflowCode = workflowCode;
        this.workflowName = workflowName;
        this.documentType = documentType;
        this.companyId = companyId;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getWorkflowCode() { return workflowCode; }
    public void setWorkflowCode(String workflowCode) { this.workflowCode = workflowCode; }
    
    public String getWorkflowName() { return workflowName; }
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public GedDocument.DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(GedDocument.DocumentType documentType) { this.documentType = documentType; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(Boolean requiresApproval) { this.requiresApproval = requiresApproval; }
    
    public Integer getApprovalLevels() { return approvalLevels; }
    public void setApprovalLevels(Integer approvalLevels) { this.approvalLevels = approvalLevels; }
    
    public Boolean getAutoApprove() { return autoApprove; }
    public void setAutoApprove(Boolean autoApprove) { this.autoApprove = autoApprove; }
    
    public Boolean getAutoArchive() { return autoArchive; }
    public void setAutoArchive(Boolean autoArchive) { this.autoArchive = autoArchive; }
    
    public Integer getArchiveDays() { return archiveDays; }
    public void setArchiveDays(Integer archiveDays) { this.archiveDays = archiveDays; }
    
    public Integer getRetentionYears() { return retentionYears; }
    public void setRetentionYears(Integer retentionYears) { this.retentionYears = retentionYears; }
    
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
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
