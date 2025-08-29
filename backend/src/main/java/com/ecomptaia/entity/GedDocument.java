package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ged_documents")
public class GedDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "document_code", unique = true, nullable = false)
    private String documentCode;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "tags")
    private String tags; // Séparés par des virgules
    
    @Column(name = "version")
    private Integer version = 1;
    
    @Column(name = "is_current_version")
    private Boolean isCurrentVersion = true;
    
    @Column(name = "parent_document_id")
    private Long parentDocumentId; // Pour le versioning
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    
    @Column(name = "security_level")
    @Enumerated(EnumType.STRING)
    private SecurityLevel securityLevel;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "is_archived")
    private Boolean isArchived = false;
    
    @Column(name = "archive_date")
    private LocalDate archiveDate;
    
    @Column(name = "retention_period_years")
    private Integer retentionPeriodYears;
    
    @Column(name = "module_reference")
    private String moduleReference; // Module lié (ASSET, INVENTORY, HR, etc.)
    
    @Column(name = "entity_reference_id")
    private Long entityReferenceId; // ID de l'entité liée
    
    @Column(name = "entity_reference_type")
    private String entityReferenceType; // Type d'entité liée
    
    @Column(name = "ecriture_id")
    private Long ecritureId; // Référence vers l'écriture comptable
    
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
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "checksum")
    private String checksum; // Pour vérifier l'intégrité
    
    @Column(name = "is_encrypted")
    private Boolean isEncrypted = false;
    
    // Enums
    public enum DocumentType {
        INVOICE,            // Facture
        RECEIPT,            // Reçu
        CONTRACT,           // Contrat
        REPORT,             // Rapport
        POLICY,             // Politique
        PROCEDURE,          // Procédure
        MANUAL,             // Manuel
        CERTIFICATE,        // Certificat
        LICENSE,            // Licence
        INSURANCE,          // Assurance
        TAX_DOCUMENT,       // Document fiscal
        LEGAL_DOCUMENT,     // Document légal
        HR_DOCUMENT,        // Document RH
        ASSET_DOCUMENT,     // Document d'immobilisation
        INVENTORY_DOCUMENT, // Document d'inventaire
        FINANCIAL_STATEMENT, // État financier
        AUDIT_REPORT,       // Rapport d'audit
        COMPLIANCE_DOCUMENT, // Document de conformité
        OTHER               // Autre
    }
    
    public enum DocumentStatus {
        DRAFT,              // Brouillon
        PENDING_APPROVAL,   // En attente d'approbation
        APPROVED,           // Approuvé
        REJECTED,           // Rejeté
        ARCHIVED,           // Archivé
        EXPIRED,            // Expiré
        DELETED             // Supprimé
    }
    
    public enum SecurityLevel {
        PUBLIC,             // Public
        INTERNAL,           // Interne
        CONFIDENTIAL,       // Confidentiel
        RESTRICTED,         // Restreint
        SECRET              // Secret
    }
    
    // Constructeurs
    public GedDocument() {
        this.createdAt = LocalDateTime.now();
        this.status = DocumentStatus.DRAFT;
        this.securityLevel = SecurityLevel.INTERNAL;
        this.isCurrentVersion = true;
        this.version = 1;
    }
    
    public GedDocument(String documentCode, String title, String fileName, String filePath, 
                   DocumentType documentType, Long companyId, String countryCode, String accountingStandard) {
        this();
        this.documentCode = documentCode;
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
        this.documentType = documentType;
        this.companyId = companyId;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDocumentCode() { return documentCode; }
    public void setDocumentCode(String documentCode) { this.documentCode = documentCode; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    
    public Boolean getIsCurrentVersion() { return isCurrentVersion; }
    public void setIsCurrentVersion(Boolean isCurrentVersion) { this.isCurrentVersion = isCurrentVersion; }
    
    public Long getParentDocumentId() { return parentDocumentId; }
    public void setParentDocumentId(Long parentDocumentId) { this.parentDocumentId = parentDocumentId; }
    
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    
    public SecurityLevel getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(SecurityLevel securityLevel) { this.securityLevel = securityLevel; }
    
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }
    
    public LocalDate getArchiveDate() { return archiveDate; }
    public void setArchiveDate(LocalDate archiveDate) { this.archiveDate = archiveDate; }
    
    public Integer getRetentionPeriodYears() { return retentionPeriodYears; }
    public void setRetentionPeriodYears(Integer retentionPeriodYears) { this.retentionPeriodYears = retentionPeriodYears; }
    
    public String getModuleReference() { return moduleReference; }
    public void setModuleReference(String moduleReference) { this.moduleReference = moduleReference; }
    
    public Long getEntityReferenceId() { return entityReferenceId; }
    public void setEntityReferenceId(Long entityReferenceId) { this.entityReferenceId = entityReferenceId; }
    
    public String getEntityReferenceType() { return entityReferenceType; }
    public void setEntityReferenceType(String entityReferenceType) { this.entityReferenceType = entityReferenceType; }
    
    public Long getEcritureId() { return ecritureId; }
    public void setEcritureId(Long ecritureId) { this.ecritureId = ecritureId; }
    
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
    
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public Boolean getIsEncrypted() { return isEncrypted; }
    public void setIsEncrypted(Boolean isEncrypted) { this.isEncrypted = isEncrypted; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

